package br.com.caelum.tarefas.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import br.com.caelum.tarefas.modelo.Tarefa;

@Repository
public class JdbcTarefaDao {
	private final Connection connection;

	@Autowired
	public JdbcTarefaDao(DataSource	dataSource)  {
		try {
			this.connection = dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void adiciona(Tarefa tarefa) {
		String sql = "insert into tarefas (descricao, finalizado) values (?,?)";
		PreparedStatement stmt;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, tarefa.getDescricao());
			stmt.setBoolean(2, tarefa.isFinalizado());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void remove(Tarefa tarefa) {

		if (tarefa.getId() == null) {
			throw new IllegalStateException("Id da tarefa não deve ser nula.");
		}

		String sql = "delete from tarefas where id = ?";
		PreparedStatement stmt;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setLong(1, tarefa.getId());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void altera(Tarefa tarefa) {
		String sql = "update tarefas set descricao = ?, finalizado = ?, dataFinalizacao = ? where id = ?";
		PreparedStatement stmt;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, tarefa.getDescricao());
			stmt.setBoolean(2, tarefa.isFinalizado());
			stmt.setDate(3,
					tarefa.getDataFinalizacao() != null ? new Date(tarefa.getDataFinalizacao().getTimeInMillis())
							: null);
			stmt.setLong(4, tarefa.getId());
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Tarefa> lista() {
		try {
			List<Tarefa> tarefas = new ArrayList<Tarefa>();
			PreparedStatement stmt = this.connection.prepareStatement("select * from tarefas");

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				// adiciona a tarefa na lista
				tarefas.add(populaTarefa(rs));
			}

			rs.close();
			stmt.close();

			return tarefas;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Tarefa buscaPorId(Long id) {

		if (id == null) {
			throw new IllegalStateException("Id da tarefa não deve ser nula.");
		}

		try {
			PreparedStatement stmt = this.connection.prepareStatement("select * from tarefas where id = ?");
			stmt.setLong(1, id);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return populaTarefa(rs);
			}

			rs.close();
			stmt.close();

			return null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void finaliza(Long id) {

		if (id == null) {
			throw new IllegalStateException("Id da tarefa não deve ser nula.");
		}

		String sql = "update tarefas set finalizado = ?, dataFinalizacao = ? where id = ?";
		PreparedStatement stmt;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setBoolean(1, true);
			stmt.setDate(2, new Date(Calendar.getInstance().getTimeInMillis()));
			stmt.setLong(3, id);
			stmt.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Tarefa populaTarefa(ResultSet rs) throws SQLException {
		Tarefa tarefa = new Tarefa();

		// popula o objeto tarefa
		tarefa.setId(rs.getLong("id"));
		tarefa.setDescricao(rs.getString("descricao"));
		tarefa.setFinalizado(rs.getBoolean("finalizado"));

		// popula a data de finalizacao da tarefa, fazendo a conversao
		Date data = rs.getDate("dataFinalizacao");
		if (data != null) {
			Calendar dataFinalizacao = Calendar.getInstance();
			dataFinalizacao.setTime(data);
			tarefa.setDataFinalizacao(dataFinalizacao);
		}
		return tarefa;
	}
}
