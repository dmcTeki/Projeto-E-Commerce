package br.ufpb.dcx.rodrigor.projetos.Produto.Repository;

import br.ufpb.dcx.rodrigor.projetos.Produto.Model.Produto;
import br.ufpb.dcx.rodrigor.projetos.database.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepository {

    private Connection conn;

    public ProdutoRepository(){
        this.conn = database.getConnection();
        criarTabela();
    }

    private void criarTabela() {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS produtos (
                    codigo IDENTITY PRIMARY KEY,
                    nome VARCHAR(255) NOT NULL,
                    descricao VARCHAR(255) NOT NULL,
                    preco DECIMAL(10,2) NOT NULL
                );
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela produtos", e);
        }
    }

    public Produto criarProduto(Produto p) {
        String sql = "INSERT INTO produtos (nome, descricao, preco) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getDescricao());
            stmt.setBigDecimal(3, p.getPreco());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                p.setCodigo(keys.getString(1));
            }

            return p;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar produto", e);
        }
    }

    public List<Produto> listarProdutos() {
        String sql = "SELECT codigo, nome, preco, descricao FROM produtos";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            List<Produto> produtos = new ArrayList<>();

            while (rs.next()) {
                Produto p = new Produto();
                p.setCodigo(rs.getString("codigo"));
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                p.setPreco(rs.getBigDecimal("preco"));
                produtos.add(p);
            }

            return produtos;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos", e);
        }
    }

    public void deletarProduto(String codigo) {
        String sql = "DELETE FROM produtos WHERE codigo = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar produto", e);
        }
    }
    public Produto buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM produtos WHERE codigo = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Produto p = new Produto();
                p.setCodigo(rs.getString("codigo"));
                p.setNome(rs.getString("nome"));
                p.setDescricao(rs.getString("descricao"));
                p.setPreco(rs.getBigDecimal("preco"));
                return p;
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por código", e);
        }
    }


}
