package br.ucsal.biblioteca;

import br.ucsal.biblioteca.controller.Biblioteca;
import br.ucsal.biblioteca.controller.LembreteDevolucaoRunnable;
import br.ucsal.biblioteca.model.Emprestimo;
import br.ucsal.biblioteca.model.Livro;
import br.ucsal.biblioteca.model.Usuario;
import br.ucsal.biblioteca.view.Console;

import java.time.LocalDate;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Biblioteca biblioteca = new Biblioteca();
        LembreteDevolucaoRunnable lembreteDevolucao = new LembreteDevolucaoRunnable(biblioteca);
        fazerCargaInicial(biblioteca);
        Console sistema = new Console(biblioteca, lembreteDevolucao);
        sistema.iniciarConsole();
    }

    private static void fazerCargaInicial(Biblioteca biblioteca) {
        Usuario pedro = new Usuario("Pedro");
        biblioteca.adicionarUsuario(pedro);
        biblioteca.adicionarUsuario(new Usuario("Maria"));
        Livro cleanCode = new Livro("Clean Code: A Handbook of Agile Software Craftsmanship", "Robert C. Martin", 2008);
        biblioteca.adicionarLivro(cleanCode);
        Livro pragmatic = new Livro("The Pragmatic Programmer: Your Journey To Mastery", "Andrew Hunt and David Thomas", 1999);
        biblioteca.adicionarLivro(pragmatic);
        biblioteca.adicionarLivro(new Livro("Introduction to Algorithms", "Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, and Clifford Stein", 1990));
        biblioteca.adicionarLivro(new Livro("Design Patterns: Elements of Reusable Object-Oriented Software", "Erich Gamma, Richard Helm, Ralph Johnson, and John Vlissides", 1994));
        biblioteca.adicionarLivro(new Livro("Structure and Interpretation of Computer Programs", "Harold Abelson and Gerald Jay Sussman", 1985));
        biblioteca.adicionarEmprestimo(new Emprestimo(pedro,cleanCode, LocalDate.of(2024,3,9)));
        biblioteca.adicionarEmprestimo(new Emprestimo(pedro,pragmatic, LocalDate.now()));
    }
}