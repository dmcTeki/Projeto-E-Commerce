package br.ufpb.dcx.rodrigor.projetos.Carrinho.Model;
import br.ufpb.dcx.rodrigor.projetos.Produto.Model.Produto;

import java.math.BigDecimal;

public class ItemCarrinho {
    private String id;
    private Produto produto;
    private int qtde;
    private BigDecimal subTotal;

    public ItemCarrinho( Produto produto, int qtde) {
        this.produto = produto;
        this.qtde = qtde;
        calcularSubtotal();
    }

    private void calcularSubtotal() {
        this.subTotal = produto.getPreco()
                .multiply(BigDecimal.valueOf(qtde));
    }

    public void incrementar() {
        qtde++;
        calcularSubtotal();
    }

    public void decrementar() {
        if (qtde > 1) {
            qtde--;
            calcularSubtotal();
        }
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQtde() {
        return qtde;
    }

    public void setQtde(int qtde) {
        this.qtde = qtde;
        calcularSubtotal();
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    @Override
    public String toString() {
        return "ItemCarrinho{" +
                "id='" + id + '\'' +
                ", produto=" + produto +
                ", qtde=" + qtde +
                ", subTotal=" + subTotal +
                '}';
    }
}
