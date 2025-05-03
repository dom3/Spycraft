package xyz.domcore.spycraft;

import org.bukkit.Bukkit;

import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class HelpBook {
    public static Book createBook() {
        return Book.book(Component.text("How to SpyCraft"), Component.text("Spycraft"), 
        Component.text("Chapter 1").decorate(TextDecoration.BOLD)
        );
    }
}
