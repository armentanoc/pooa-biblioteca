package br.ucsal.biblioteca.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import br.ucsal.biblioteca.model.Emprestimo;

public class LembreteDevolucaoRunnable implements Runnable {

	private final Biblioteca biblioteca;
	private static final long INTERVALO_HORAS = 24;

	public LembreteDevolucaoRunnable(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	@Override
	public void run() {
		while (true) {
			enviarLembretesDevolucao();
			try {
				Thread.sleep(INTERVALO_HORAS * 60 * 60 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void enviarLembretesDevolucao() {
		LocalDate hoje = LocalDate.now();
		for (Emprestimo emprestimo : biblioteca.getEmprestimos()) {
			long diasRestantes = ChronoUnit.DAYS.between(hoje, emprestimo.getDataDevolucao());
			if (diasRestantes <= 1) {
				System.out.println("--------------------------------------------------");
				System.out.println("Lembrete para " + emprestimo.getUsuario().getNome() + ":");
				if (diasRestantes >= 0) {
					System.out.println("O livro '" + emprestimo.getLivro().getTitulo() + "' foi emprestado em "
							+ emprestimo.getDataRetirada() + ". Você tem " + diasRestantes
							+ " dia(s) restantes para devolvê-lo antes do prazo em " + emprestimo.getDataDevolucao()
							+ ".");
				} else {
					System.out.println("O livro '" + emprestimo.getLivro().getTitulo() + "' foi emprestado em "
							+ emprestimo.getDataRetirada() + ". O prazo de devolução era "
							+ emprestimo.getDataDevolucao() + ". Por favor, devolva o livro o quanto antes.");
				}
			}
		}
	}
}
