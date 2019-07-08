import dao.AluguelDAO;
import dao.ClienteDAO;
import dao.FilmeDAO;
import dao.jdbc.AluguelDAOImpl;
import dao.jdbc.ClienteDAOImpl;
import dao.jdbc.FilmeDAOImpl;
import entidades.Aluguel;
import entidades.Cliente;
import entidades.Filme;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;


public class Main {

    public static void main(String[] args) {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/teste-estagio", "postgres", "admin");
            conn.setAutoCommit(false);

            //Demonstrar o funcionamento aqui
            ClienteDAO clienteDAO = new ClienteDAOImpl();
            FilmeDAO filmeDAO = new FilmeDAOImpl();
            AluguelDAO aluguelDAO = new  AluguelDAOImpl();
            
            // System.out.println(clienteDAO.list(conn));
            // System.out.println(filmeDAO.list(conn));
            // System.out.println(aluguelDAO.list(conn));
            
            Cliente cliente = new Cliente("João Porto");
            clienteDAO.insert(conn, cliente);
            
            // System.out.println((clienteDAO.find(conn, cliente.getIdCliente())));
            
            Filme filme = new Filme();
            filme.setDataLancamento(new Date(2012,12,11));
            filme.setNome("Django Unchained");
            filme.setDescricao("American revisionist Western");
            filmeDAO.insert(conn, filme);
            
            // System.out.println((filmeDAO.find(conn, filme.getIdFilme())));
            
            ArrayList<Filme> filmes = new ArrayList<Filme>();
            filmes.add(filme);
            
            filme = new Filme();
            filme.setDataLancamento(new Date(1957,10,4));
            filme.setNome("12 Angry Men");
            filme.setDescricao("Legal drama");
            filmeDAO.insert(conn, filme);
            
            // System.out.println((filmeDAO.find(conn, filme.getIdFilme())));
            
            filmes.add(filme);
            
            Aluguel aluguel = new Aluguel();
            aluguel.setFilmes(filmes);
            aluguel.setCliente(cliente);
            aluguel.setDataAluguel(new Date(2019,7,8));
            aluguel.setValor(60);
            aluguelDAO.insert(conn, aluguel);
            
            // System.out.println((aluguelDAO.find(conn, aluguel.getIdAluguel())));
            
            cliente.setNome("João V. A. Porto");
            filme.setDescricao("American courtroom drama");
            
            clienteDAO.edit(conn, cliente);
            filmeDAO.edit(conn, filme);
            
            filmes.clear();
            filmes.add(filme);
            
            aluguel.setFilmes(filmes);
            aluguel.setValor(30);
            
            aluguelDAO.edit(conn, aluguel);
            
            // System.out.println((aluguelDAO.find(conn, aluguel.getIdAluguel())));
            
            clienteDAO.delete(conn, cliente.getIdCliente());
            filmeDAO.delete(conn, filme.getIdFilme());
            aluguelDAO.delete(conn, aluguel);
            
            System.out.println(clienteDAO.list(conn) + "\n");
            System.out.println(filmeDAO.list(conn)  + "\n");
            System.out.println(aluguelDAO.list(conn)  + "\n");
            
            /*
             * Ao final, só permanecera no BD o registro do filme "Django Unchained"
             */
            
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Fim do teste.");
    }
}