package br.ufpb.dcx.rodrigor.projetos.Carrinho.Controllers;

import br.ufpb.dcx.rodrigor.projetos.Carrinho.Model.Carrinho;
import br.ufpb.dcx.rodrigor.projetos.Carrinho.Service.CarrinhoService;
import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.Produto.Model.Produto;
import br.ufpb.dcx.rodrigor.projetos.Produto.Services.ProdutoService;
import br.ufpb.dcx.rodrigor.projetos.login.LoginController;
import io.javalin.http.Context;
import org.eclipse.jetty.security.LoginService;

import java.util.*;
public class CarrinhoController {

    private final CarrinhoService carrinhoService;
    private final ProdutoService produtoService;




    public CarrinhoController(CarrinhoService carrinhoService,
                              ProdutoService produtoService) {
        this.carrinhoService = carrinhoService;
        this.produtoService = produtoService;

    }

    public void mostrarCarrinho(Context ctx) {
        Carrinho carrinho = carrinhoService.getCarrinho(ctx);

        if (carrinho.getItens() == null) {
            carrinho.setItens(new ArrayList<>());
        }
        boolean carrinhoVazio = carrinho.getItens().isEmpty();

        ctx.attribute("carrinho", carrinho);
        ctx.attribute("carrinhoVazio", carrinhoVazio);

        ctx.render("/produtos/Carrinho.html");
    }

        public void adicionaritem(Context ctx) {
        String codigoProduto = ctx.pathParam("codigo");
        Produto produto = produtoService.buscarPorCodigo(codigoProduto);
        if (produto == null) {
            ctx.redirect("/home");
            return;
        }
        carrinhoService.adicionarItem(ctx, produto);
        String referer = ctx.header("Referer");
        if (referer != null && referer.contains("/carrinho")) {
            ctx.redirect("/carrinho");
        } else {
            if (ctx.sessionAttribute("usuario") != null) {
                ctx.redirect("/principal-produtos");
            } else {
                ctx.redirect("/home");
            }
        }

    }
    public void diminuirItem(Context ctx) {
        String codigoProduto = ctx.pathParam("codigo");

        carrinhoService.diminuirProduto(ctx, codigoProduto);
        ctx.redirect("/carrinho");
    }

    public void removerItem(Context ctx) {
        String codigoProduto = ctx.pathParam("codigo");

        carrinhoService.removerProduto(ctx, codigoProduto);
        ctx.redirect("/carrinho");
    }

    public void finalizarPedido(Context ctx) {
        ctx.attribute("pedidoFinalizado", true);
        carrinhoService.excluirCarrinho(ctx);
        ctx.render("Produtos/carrinho_finalizado.html");
    }
    }


