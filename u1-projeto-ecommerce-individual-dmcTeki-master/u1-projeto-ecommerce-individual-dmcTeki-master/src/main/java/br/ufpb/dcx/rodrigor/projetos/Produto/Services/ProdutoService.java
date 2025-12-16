package br.ufpb.dcx.rodrigor.projetos.Produto.Services;

import java.util.List;
import br.ufpb.dcx.rodrigor.projetos.Produto.Model.Produto;
import br.ufpb.dcx.rodrigor.projetos.Produto.Repository.ProdutoRepository;
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(){
        this.produtoRepository = new ProdutoRepository();
    }

    public void cadastrarProduto( Produto produto){
        if (produto == null){
            throw new IllegalArgumentException("Produto inválido");
        }
        if (produto.getPreco() == null || produto.getPreco().doubleValue() <= 0) {
            throw new IllegalArgumentException("Preço inválido");
        }
        produtoRepository.criarProduto(produto);
    }
    public void removerProduto(String codigo){
        if(codigo == null){
            throw new IllegalArgumentException("Código do produto inválido");
        }
        produtoRepository.deletarProduto(codigo);
    }

    public List<Produto> listarProdutos(){
        return produtoRepository.listarProdutos();
    }
    public Produto buscarPorCodigo(String codigo) {
        return produtoRepository.buscarPorCodigo(codigo);
    }

}



