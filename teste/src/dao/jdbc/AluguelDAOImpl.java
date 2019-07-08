package dao.jdbc;

import dao.AluguelDAO;
import dao.ClienteDAO;
import dao.FilmeDAO;
import entidades.Aluguel;
import entidades.Cliente;
import entidades.Filme;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;


public class AluguelDAOImpl implements AluguelDAO {

    @Override
    public void insert(Connection conn, Aluguel aluguel) throws Exception {

        PreparedStatement myStmt = conn.prepareStatement("insert into en_aluguel (id_aluguel, id_cliente, data_aluguel, valor) values (?, ?, ?, ?)");

        Integer idAluguel = this.getNextId(conn);

        myStmt.setInt(1, idAluguel);
        myStmt.setInt(2, aluguel.getCliente().getIdCliente());
        myStmt.setDate(3, new Date(aluguel.getDataAluguel().getTime()));
        myStmt.setFloat(4, aluguel.getValor());

        myStmt.execute();
        
        aluguel.setIdAluguel(idAluguel);
        
        insertReAluguelFilme(conn, aluguel);
        
        conn.commit();
    }

    @Override
    public void edit(Connection conn, Aluguel aluguel) throws Exception {
    	Integer idAluguel = aluguel.getIdAluguel();
        PreparedStatement myStmt = conn.prepareStatement("update en_aluguel set id_cliente = (?) where id_aluguel = (?)");

        myStmt.setInt(1, aluguel.getCliente().getIdCliente());
        myStmt.setInt(2, idAluguel);

        myStmt.execute();
        
        myStmt = conn.prepareStatement("update en_aluguel set data_aluguel = (?) where id_aluguel = (?)");
        
        myStmt.setDate(1, new Date(aluguel.getDataAluguel().getTime()));
        myStmt.setInt(2, idAluguel);
        
        myStmt.execute();
        
        myStmt = conn.prepareStatement("update en_aluguel set valor = (?) where id_aluguel = (?)");
        
        myStmt.setFloat(1, aluguel.getValor());
        myStmt.setInt(2, idAluguel);
        
        myStmt.execute();
        
        myStmt = conn.prepareStatement("delete from re_aluguel_filme where id_aluguel = ?");
        
        myStmt.setInt(1, idAluguel);
        
        myStmt.execute();
        
        insertReAluguelFilme(conn, aluguel);
        
        conn.commit();
    }
    
    private void insertReAluguelFilme(Connection conn, Aluguel aluguel) throws SQLException {
    	PreparedStatement myStmt;
    	
    	for (Filme filme : aluguel.getFilmes()) {
        	myStmt = conn.prepareStatement("insert into re_aluguel_filme(id_filme, id_aluguel) values (?, ?)");
        	
        	myStmt.setInt(1, filme.getIdFilme());
        	myStmt.setInt(2, aluguel.getIdAluguel());
        	
        	myStmt.execute();
        }
    }

    @Override
    public void delete(Connection conn, Aluguel aluguel) throws Exception {
        
        PreparedStatement myStmt = conn.prepareStatement("delete from re_aluguel_filme where id_aluguel = ?");

        myStmt.setInt(1, aluguel.getIdAluguel());
        
        myStmt.execute();
        
        myStmt = conn.prepareStatement("delete from en_aluguel where id_aluguel = ?");

        myStmt.setInt(1, aluguel.getIdAluguel());
        
        myStmt.execute();
        conn.commit();

    }

    @Override
    public Collection<Aluguel> list(Connection conn) throws Exception {

        PreparedStatement myStmt = conn.prepareStatement("select * from en_aluguel order by id_aluguel");
        ResultSet myRs = myStmt.executeQuery();

        Collection<Aluguel> items = new ArrayList<>();

        while (myRs.next()) 
            items.add(this.find(conn, myRs.getInt("id_aluguel")));

        return items;
    }

    @Override
    public Aluguel find(Connection conn, Integer idAluguel) throws Exception {
    	ClienteDAO clienteDAO = new ClienteDAOImpl();
    	FilmeDAO filmeDAO = new FilmeDAOImpl(); 
    	
        PreparedStatement myStmt = conn.prepareStatement("select * from en_aluguel where id_aluguel = ?");

        myStmt.setInt(1, idAluguel);

        ResultSet myRs = myStmt.executeQuery();

        if (!myRs.next()) {
            return null;
        }

        Cliente cliente = clienteDAO.find(conn, myRs.getInt("id_cliente"));
        java.util.Date dataAluguel = new java.util.Date(myRs.getDate("data_aluguel").getTime());
        float valor = myRs.getFloat("valor");
        
        myStmt = conn.prepareStatement("select * from re_aluguel_filme where id_aluguel = ?");
        
        myStmt.setInt(1, idAluguel);
        
        myRs = myStmt.executeQuery();

        ArrayList<Filme> filmes = new ArrayList<>();

        while (myRs.next()) {
        	Filme filme = filmeDAO.find(conn, myRs.getInt("id_filme"));
        	filmes.add(filme);
        }
     
        return new Aluguel(idAluguel, filmes, cliente, dataAluguel, valor);
    }

    @Override
    public Integer getNextId(Connection conn) throws Exception {
        PreparedStatement myStmt = conn.prepareStatement("select nextval('seq_en_aluguel')");
        ResultSet rs = myStmt.executeQuery();
        rs.next();
        return rs.getInt(1);
    }
}

