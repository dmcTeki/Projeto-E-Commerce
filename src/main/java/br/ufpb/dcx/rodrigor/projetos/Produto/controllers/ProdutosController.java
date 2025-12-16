package br.ufpb.dcx.rodrigor.projetos.Produto.controllers;

import java.math.BigDecimal;

import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.Produto.Model.Produto;
import br.ufpb.dcx.rodrigor.projetos.Produto.Services.ProdutoService;
import io.javalin.http.Context;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ProdutosController {
    private static final Logger logger = LogManager.getLogger(ProdutosController.class);
    private ProdutoService produtoService;

    public ProdutosController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    public void mostrarLista(Context ctx){
        ctx.render("/produtos/lista_produtos.html");
    }

    public void mostrarFormCadastro(Context ctx){
        ctx.render("/produtos/form_produto.html");
    }
    public void mostrarHome(Context ctx) {
        ctx.attribute("produtos", produtoService.listarProdutos());
        ctx.render("/home.html");
    }

    public void produtosPrincipal(Context ctx) {
        ctx.attribute("produtos", produtoService.listarProdutos());
        ctx.render("/produtos/principal_produtos.html");
    }

    public void listarProdutos(Context ctx) {
        ProdutoService produtoService = ctx.appData(Keys.PRODUTO_SERVICE.key());
        try {
            List<Produto> produtos = produtoService.listarProdutos();
            ctx.attribute("produtos", produtos);
            ctx.render("/produtos/lista_produtos.html");
        } catch (Exception e) {
            logger.error("Erro ao recuperar produtos", e);
            ctx.status(500).result("Erro ao recuperar produtos: " + e.getMessage());
        }
    }

    public void adicionarProduto(Context ctx){
        ProdutoService produtoService = ctx.appData(Keys.PRODUTO_SERVICE.key());
        String nome = ctx.formParam("nome");
        String descricao = ctx.formParam("descricao");
        String precoString = ctx.formParam("preco");
        try{
            if(nome == null || precoString == null) {
                ctx.status(400).result("Nome e preço são Obrigatórios");
            }
            BigDecimal preco = new BigDecimal(precoString);
            produtoService.cadastrarProduto(new Produto(null,nome, descricao, preco));
            ctx.attribute("Salvo","Produto cadastrado com sucesso.");
            ctx.redirect("/produtos");
        } catch (Exception e) {
            logger.error("Erro ao adicionar produto", e);
            ctx.attribute("erro", "Erro: " + "Preço Inválido");
            ctx.render("/produtos/form_produto.html");
        }

    }
    public void removerProduto(Context ctx){
        ProdutoService produtoService = ctx.appData(Keys.PRODUTO_SERVICE.key());
        String codigo = ctx.pathParam("codigo");
        produtoService.removerProduto(codigo);
        ctx.redirect("/produtos");
    }
}
