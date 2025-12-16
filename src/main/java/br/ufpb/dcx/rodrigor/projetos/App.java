package br.ufpb.dcx.rodrigor.projetos;

import br.ufpb.dcx.rodrigor.projetos.Carrinho.Controllers.CarrinhoController;
import br.ufpb.dcx.rodrigor.projetos.Carrinho.Model.Carrinho;
import br.ufpb.dcx.rodrigor.projetos.Carrinho.Model.ItemCarrinho;
import br.ufpb.dcx.rodrigor.projetos.Carrinho.Service.CarrinhoService;
import br.ufpb.dcx.rodrigor.projetos.Produto.Model.Produto;
import br.ufpb.dcx.rodrigor.projetos.Produto.Repository.ProdutoRepository;
import br.ufpb.dcx.rodrigor.projetos.Produto.Services.ProdutoService;
import br.ufpb.dcx.rodrigor.projetos.Produto.controllers.ProdutosController;
import br.ufpb.dcx.rodrigor.projetos.database.H2Console;
import br.ufpb.dcx.rodrigor.projetos.form.controller.FormController;
import br.ufpb.dcx.rodrigor.projetos.form.services.FormService;
import br.ufpb.dcx.rodrigor.projetos.login.LoginController;
import br.ufpb.dcx.rodrigor.projetos.login.UsuarioController;
import br.ufpb.dcx.rodrigor.projetos.login.UsuarioService;
import br.ufpb.dcx.rodrigor.projetos.participante.controllers.ParticipanteController;
import br.ufpb.dcx.rodrigor.projetos.participante.services.ParticipanteService;
import br.ufpb.dcx.rodrigor.projetos.projeto.controllers.ProjetoController;
import br.ufpb.dcx.rodrigor.projetos.projeto.services.ProjetoService;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Consumer;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    private static final int PORTA_PADRAO = 8000;

    //Propriedades do application.properties:
    private static final String PROP_PORTA_SERVIDOR = "porta.servidor";

    private final Properties propriedades;

    public App() {
        this.propriedades = carregarPropriedades();
    }

    public void iniciar() {
        Javalin app = inicializarJavalin();
        configurarPaginasDeErro(app);
        configurarRotas(app);

        // Lidando com exceções não tratadas
        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Erro não tratado", e);
            ctx.status(500);
        });
    }

    private void configurarPaginasDeErro(Javalin app) {
        app.error(404, ctx -> ctx.render("erro_404.html"));
        app.error(500, ctx -> ctx.render("erro_500.html"));
    }

    private Javalin inicializarJavalin() {
        int porta = obterPortaServidor();

        logger.info("Iniciando aplicação na porta {}", porta);

        Consumer<JavalinConfig> configConsumer = this::configureJavalin;

        return Javalin.create(configConsumer).start(porta);
    }

    private void configureJavalin(JavalinConfig config) {
        TemplateEngine templateEngine = configurarThymeleaf();

        config.events(event -> {
            event.serverStarting(() -> {
                logger.info("Servidor Javalin está iniciando...");
                registrarServicos(config);
            });
            event.serverStopping(() -> {
            });
        });
        config.staticFiles.add(staticFileConfig -> {
            staticFileConfig.directory = "/public";
            staticFileConfig.location = Location.CLASSPATH;
        });
        config.fileRenderer(new JavalinThymeleaf(templateEngine));

    }

    private void registrarServicos(JavalinConfig config) {
        ParticipanteService participanteService = new ParticipanteService("http://localhost:8001");
        config.appData(Keys.DB_CONNECTION.key(), br.ufpb.dcx.rodrigor.projetos.database.database.getConnection());
        config.appData(Keys.PROJETO_SERVICE.key(), new ProjetoService(participanteService));
        config.appData(Keys.PARTICIPANTE_SERVICE.key(), participanteService);
        config.appData(Keys.USUARIO_SERVICE.key(), new UsuarioService());
        config.appData(Keys.FORM_SERVICE.key(), new FormService());
        config.appData(Keys.PRODUTO_SERVICE.key(), new ProdutoService());
        config.appData(Keys.CARRINHO_SERVICE.key(), new CarrinhoService());


    }


    private void configurarRotas(Javalin app) {

        LoginController loginController = new LoginController();

        app.get("/", ctx -> ctx.redirect("/home"));
        app.get("/login", loginController::mostrarPaginaLogin);
        app.post("/login", loginController::processarLogin);
        app.get("/logout", loginController::logout);

        app.get("/area-interna", ctx -> {
            if (ctx.sessionAttribute("usuario") == null) {
                ctx.redirect("/home");
            } else {
                ctx.render("area_interna.html");
            }
        });

        // Rotas para o controlador de usuário
        UsuarioController usuarioController = new UsuarioController();

        app.before("/usuarios", ctx -> {
            if (!loginController.isLogged(ctx)) {
                ctx.redirect("/login");
                return;
            }
        });

        app.get("/usuarios", usuarioController::listarUsuarios);
        app.get("/usuarios/novo", usuarioController::mostrarFormularioCadastro);
        app.post("/usuarios/cadastrar", usuarioController::cadastrarUsuario);
        app.get("/usuarios/signup", usuarioController::mostrarFormulario_signup);
        app.get("/usuarios/{id}/remover", usuarioController::removerUsuario);

        // Rotas para o controlador de Produtos
        ProdutoService produtoService = new ProdutoService();
        ProdutosController produtosController = new ProdutosController(produtoService);

        app.before("/produtos", ctx -> {
            if (!loginController.isLogged(ctx)) {
                ctx.redirect("/login");
                return;
            }
        });

        app.before("/produtos/*", ctx -> {
            if (!loginController.isLogged(ctx)) {
                ctx.redirect("/login");
                return;
            }
        });

        app.get("/produtos", produtosController::listarProdutos);
        app.get("/principal-produtos", produtosController::produtosPrincipal);
        app.get("/produtos/novo",produtosController::mostrarFormCadastro);
        app.get("/home",produtosController::mostrarHome);
        app.post("/produtos",produtosController :: adicionarProduto);
        app.get("/produtos/{codigo}/remover", produtosController ::removerProduto);

        // Rotas para o controlador de formulários

        app.before("/cadastros", ctx -> {
            if (!loginController.isLogged(ctx)) {
                ctx.redirect("/login");
                return;
            }
        });
        FormController formController = new FormController();
        app.get("/form/{formId}", formController::abrirFormulario);
        app.post("/form/{formId}", formController::validarFormulario);
        app.get("/cadastros", formController::mostrarCadastros);

        //rotas para o controlador de carrinho
        CarrinhoService carrinhoService = new CarrinhoService();
        CarrinhoController carrinhoController = new CarrinhoController(carrinhoService, produtoService);

        app.get("/carrinho", carrinhoController::mostrarCarrinho);
        app.get("/carrinho_adicionar/{codigo}", carrinhoController::adicionaritem);
        app.get("/carrinho_diminuir/{codigo}", carrinhoController::diminuirItem);
        app.get("/carrinho_remover/{codigo}", carrinhoController::removerItem);

        app.before("/carrinho-finalizar", ctx -> {
            if (!loginController.isLogged(ctx)) {
                ctx.redirect("/login");
                return;
            }
        });
        app.get("/carrinho-finalizar",carrinhoController::finalizarPedido);

    }



    private int obterPortaServidor() {
        if (propriedades.containsKey(PROP_PORTA_SERVIDOR)) {
            try {
                return Integer.parseInt(propriedades.getProperty(PROP_PORTA_SERVIDOR));
            } catch (NumberFormatException e) {
                logger.error("Porta definida no arquivo de propriedades não é um número válido: '{}'", propriedades.getProperty(PROP_PORTA_SERVIDOR));
                System.exit(1);
            }
        } else {
            logger.info("Porta não definida no arquivo de propriedades, utilizando porta padrão {}", PORTA_PADRAO);
        }
        return PORTA_PADRAO;
    }

    private TemplateEngine configurarThymeleaf() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

    private Properties carregarPropriedades() {
        Properties prop = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            if(input == null){
                logger.error("Arquivo de propriedades /src/main/resources/application.properties não encontrado");
                logger.error("Use o arquivo application.properties.examplo como base para criar o arquivo application.properties");
                System.exit(1);
            }
            prop.load(input);
        } catch (IOException ex) {
            logger.error("Erro ao carregar o arquivo de propriedades /src/main/resources/application.properties", ex);
            System.exit(1);
        }
        return prop;
    }

    public static void main(String[] args) {
        try {
            new App().iniciar();
            new H2Console().start();
        } catch (Exception e) {
            logger.error("Erro ao iniciar a aplicação", e);
            System.exit(1);
        }
    }
}