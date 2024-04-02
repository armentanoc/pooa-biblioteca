package br.ucsal.biblioteca.view;

import br.ucsal.biblioteca.controller.Biblioteca;
import br.ucsal.biblioteca.controller.LembreteDevolucaoRunnable;
import br.ucsal.biblioteca.model.Emprestimo;
import br.ucsal.biblioteca.model.Livro;
import br.ucsal.biblioteca.model.Usuario;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

// Classes Livro e Usuario como fornecido anteriormente

public class Console {

	private final Scanner scanner = new Scanner(System.in);

	private final Biblioteca biblioteca;

	private final LembreteDevolucaoRunnable lembreteDevolucao;

	public Console(Biblioteca biblioteca, LembreteDevolucaoRunnable lembreteDevolucao) {
		this.biblioteca = biblioteca;
		this.lembreteDevolucao = lembreteDevolucao;
	}

	public void iniciarConsole() {
		Thread lembreteThread = new Thread(lembreteDevolucao);
		lembreteThread.start();

		boolean sair = false;
		while (!sair) {
			System.out.println("\n--- Sistema de Gerenciamento de Biblioteca ---");
			System.out.println("1. Adicionar Livro");
			System.out.println("2. Adicionar Usuário");
			System.out.println("3. Empréstimo de Livro");
			System.out.println("4. Devolução de Livro");
			System.out.println("5. Listar Usuários");
			System.out.println("6. Listar Livros");
			System.out.println("7. Sair");
			System.out.print("Escolha uma opção: \n");
			int opcao = scanner.nextInt();
			scanner.nextLine(); // Consumir nova linha

			switch (opcao) {
			case 1:
				adicionarLivroConsole();
				break;
			case 2:
				adicionarUsuarioConsole();
				break;
			case 3:
				emprestarLivroConsole();
				break;
			case 4:
				devolverLivroConsole();
				break;
			case 5:
				listarEntidadesConsole(biblioteca.getUsuarios());
				break;
			case 6:
				listarEntidadesConsole(biblioteca.getLivros());
				break;
			case 7:
				sair = true;
				break;
			default:
				System.out.println("Opção inválida.");
				break;
			}
		}
		scanner.close();
	}

	private <T> void listarEntidadesConsole(List<T> entidades) {
		System.out.println("\n--- Listar " + entidades.get(0).getClass().getSimpleName() + " ---");
		for (T entidade : entidades) {
			Class<?> entidadeClass = entidade.getClass();
			System.out.println(entidadeClass.getSimpleName() + ":");
			for (Field field : entidadeClass.getDeclaredFields()) {
				field.setAccessible(true);
				try {
					System.out.println(field.getName() + ": " + field.get(entidade));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			System.out.println("-------------");
		}
	}

	private void adicionarLivroConsole() {
		try {
			System.out.println("\n--- Adicionar Livro ---");
			System.out.print("Título: ");
			String titulo = scanner.nextLine();

			System.out.print("Autor: ");
			String autor = scanner.nextLine();
			System.out.print("Ano de Publicação: ");
			int ano = scanner.nextInt();
			Livro livro = new Livro(titulo, autor, ano);
			if (livro.getTitulo().length() < 3) {
				System.out.println("Erro: O título deve ter pelo menos 3 caracteres.");
			} else {
				biblioteca.adicionarLivro(livro);
				System.out.println("ID do Livro " + livro.getId());
				System.out.println("Livro adicionado com sucesso.");
			}
		} catch (Exception e) {
			System.out.println("Erro: " + e.getMessage());
		}
	}

	private void adicionarUsuarioConsole() {
		try {
			System.out.println("\n--- Registrar Usuário ---");
			System.out.print("Nome: ");
			String nome = scanner.nextLine();
			Usuario usuario = new Usuario(nome);
			if (usuario.getNome().length() < 3) {
				System.out.println("Erro: O título deve ter pelo menos 3 caracteres.");
			} else {
				biblioteca.adicionarUsuario(usuario);
				System.out.println("ID Usuário: " + usuario.getId());
				System.out.println("Usuário registrado com sucesso.");
			}
		} catch (Exception e) {
			System.out.println("Erro: " + e.getMessage());
		}
	}

	private void emprestarLivroConsole() {
		System.out.println("\n--- Empréstimo de Livro ---");

		Usuario usuario = null;
		while (usuario == null) {
			System.out.print("ID do Usuário: ");
			int idUsuario = scanner.nextInt();
			scanner.nextLine(); // Limpa o buffer do scanner
			usuario = biblioteca.getUsuarios().stream().filter(u -> u.getId() == idUsuario).findFirst().orElse(null);
			if (usuario == null) {
				System.out.println("Usuário não encontrado.");
			}
			if (idUsuario == -1) {
				break;
			}
		}

		if (usuario != null) {
			System.out.println("Usuário: " + usuario.getNome());

			System.out.print("ID do Livro: ");
			int idLivro = scanner.nextInt();
			scanner.nextLine(); // Limpa o buffer do scanner

			Livro livroSelecionado = biblioteca.getLivros().stream().filter(l -> l.getId() == idLivro).findFirst()
					.orElse(null);

			if (livroSelecionado == null) {
				System.out.println("Livro não encontrado.");
				return;
			}

			if (!livroSelecionado.isDisponivel()) {
				System.out.println("Livro não disponível.");
				return;
			}

			System.out.println("Livro: " + livroSelecionado.getTitulo() + ". Confirmar empréstimo? (s/n)");
			String confirmacao = scanner.nextLine();
			if (confirmacao.equalsIgnoreCase("s")) {
				livroSelecionado.setDisponivel(false);
				biblioteca.adicionarEmprestimo(new Emprestimo(usuario, livroSelecionado, LocalDate.now()));
				System.out.println("Emprestimo efetuado com sucesso.");
			} else {
				System.out.println("Emprestimo cancelado.");
			}

		}
	}

	private void devolverLivroConsole() {
		System.out.println("\n--- Devolução de Livro ---");
		System.out.print("ID do Livro: ");
		int idLivro = scanner.nextInt();
		scanner.nextLine(); // Limpa o buffer do scanner

		Emprestimo emprestimoParaRemover = biblioteca.getEmprestimos().stream()
				.filter(e -> e.getLivro().getId() == idLivro && !e.getLivro().isDisponivel()).findFirst().orElse(null);

		if (emprestimoParaRemover == null) {
			System.out.println("Empréstimo não encontrado ou livro já está disponível.");
			return;
		}

		Livro livroParaDevolver = emprestimoParaRemover.getLivro();
		Usuario usuarioQueDevolve = emprestimoParaRemover.getUsuario();
		System.out
				.println("Livro: " + livroParaDevolver.getTitulo() + " emprestado por " + usuarioQueDevolve.getNome());
		System.out.println("Data de devolução prevista: " + emprestimoParaRemover.getDataDevolucao());

		System.out.println("Confirmar devolução? (s/n)");
		String confirmacao = scanner.nextLine().trim().toLowerCase();

		if (!confirmacao.equals("s")) {
			System.out.println("Devolução cancelada.");
			return;
		}

		livroParaDevolver.setDisponivel(true);
		biblioteca.removerEmprestimo(emprestimoParaRemover);
		System.out.println("Livro devolvido com sucesso.");
	}

	private void enviarLembretesDevolucao() {
		System.out.println("\n--- Enviando Lembretes de Devolução ---");
		this.lembreteDevolucao.run();
		System.out.println("\n--- Lembretes de Devolução Enviados---");
	}

}
