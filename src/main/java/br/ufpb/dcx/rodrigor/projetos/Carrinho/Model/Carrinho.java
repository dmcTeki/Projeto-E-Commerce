package br.ufpb.dcx.rodrigor.projetos.Carrinho.Model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import br.ufpb.dcx.rodrigor.projetos.Carrinho.Model.ItemCarrinho;
import br.ufpb.dcx.rodrigor.projetos.Produto.Model.Produto;

import javax.naming.Context;

public class Carrinho {

    private List<ItemCarrinho> itens = new ArrayList<>();
    private BigDecimal valorTotal;

    public void adicionarItem(ItemCarrinho novoItem) {
        Optional<ItemCarrinho> itemExiste = itens.stream().filter
                (i -> i.getProduto().getCodigo().
                        equals(novoItem.getProduto().getCodigo())).findFirst();
        if (itemExiste.isPresent()) {
            ItemCarrinho item = itemExiste.get();
            item.setQtde(item.getQtde() + novoItem.getQtde());
        } else {
            this.itens.add(novoItem);
        }
    }

    public void removerProduto(String codigoProduto) {
        itens.removeIf(item ->
                item.getProduto().getCodigo().equals(codigoProduto)
        );
    }

    public void diminuirProduto(String produtoId) {
        for (ItemCarrinho item : itens) {
            if (item.getQtde() == 1) {
                removerProduto(item.getProduto().getCodigo());
            }
            if (item.getProduto().getCodigo().equals(produtoId)) {
                item.decrementar();
                return;
            }
        }
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrinho item : itens) {
            total = total.add(item.getSubTotal());
        }
        return total;
    }

    public Carrinho() {
    }


    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public void setItens(List<ItemCarrinho> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void limpar() {
        itens.clear();
    }
}
