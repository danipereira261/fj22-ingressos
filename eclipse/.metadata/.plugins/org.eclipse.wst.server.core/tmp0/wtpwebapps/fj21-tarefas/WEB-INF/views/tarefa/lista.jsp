<%@	taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@	taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="resources/jquery.js"></script>
</head>
<body>
	<script 
		type="text/javascript">
		
		function finalizaAgora(id) {
			console.log("entrou no JS");
			console.log(id);
			$.post("finalizaTarefa",{'id':id},	function() {
					$("#tarefa_"+id).html("Finalizado");
				});
		}
	</script>

	<a href="novaTarefa">Criar nova tarefa</a>

	<br />
	<br />


	<table>
		<tr>
			<th>Id</th>
			<th>Descrição</th>
			<th>Finalizado?</th>
			<th>Data de finalização</th>
		</tr>
		<c:forEach items="${tarefas}" var="tarefa">
			<tr>
				<td>${tarefa.id}</td>
				<td>${tarefa.descricao}</td>
				<c:if test="${tarefa.finalizado	eq	false}">
					<td>Não finalizado</td>
				</c:if>
				<c:if test="${tarefa.finalizado	eq	false}">
					<td id="tarefa_${tarefa.id}">
					<a href="#" onClick="finalizaAgora(${tarefa.id})"> Finaliza agora! </a>
					</td>
				</c:if>
				<td><fmt:formatDate value="${tarefa.dataFinalizacao.time}"
						pattern="dd/MM/yyyy" /></td>
				<td><a href="removeTarefa?id=${tarefa.id}">Remover</a></td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>