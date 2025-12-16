package br.ufpb.dcx.rodrigor.projetos.Carrinho.Service;

import br.ufpb.dcx.rodrigor.projetos.Carrinho.Model.Carrinho;
import br.ufpb.dcx.rodrigor.projetos.Carrinho.Model.ItemCarrinho;
import br.ufpb.dcx.rodrigor.projetos.Keys;
import br.ufpb.dcx.rodrigor.projetos.Produto.Model.Produto;
import br.ufpb.dcx.rodrigor.projetos.Produto.Repository.ProdutoRepository;
import io.javalin.http.Context;

public class CarrinhoService {
    private final String CARRINHO_KEY = String.valueOf(Keys.CARRINHO_SESSION_KEY.key());


    public Carrinho getCarrinho(Context ctx) {
        Carrinho carrinho = ctx.sessionAttribute(CARRINHO_KEY);
        if (carrinho == null) {
            carrinho = new Carrinho();
        }
        return carrinho;
    }

    public void adicionarItem(Context ctx, Produto produto) {
        Carrinho carrinho = getCarrinho(ctx);
        ItemCarrinho item = new ItemCarrinho( produto, 1);
        carrinho.adicionarItem(item);
        ctx.sessionAttribute(CARRINHO_KEY, carrinho);
        System.out.println("Produto adicionado: " + produto.getNome());
    }

    public void diminuirProduto(Context ctx, String codigoProduto) {
        Carrinho carrinho = getCarrinho(ctx);
        carrinho.diminuirProduto(codigoProduto);
        ctx.sessionAttribute(CARRINHO_KEY, carrinho);
    }

    public void removerProduto(Context ctx, String codigoProduto) {
        Carrinho carrinho = getCarrinho(ctx);
        carrinho.removerProduto(codigoProduto);
        ctx.sessionAttribute(CARRINHO_KEY, carrinho);
    }

    public void excluirCarrinho(Context ctx) {
        Carrinho carrinho =  getCarrinho(ctx);
        if (carrinho != null) {
            carrinho.limpar();
            ctx.sessionAttribute("carrinho", carrinho);
        }
    }
}

