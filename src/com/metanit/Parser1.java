package com.metanit;

import java.util.List;
import java.util.Scanner;

/**
 * Грамматический разбор грамматики
 * выражение ::= ЧИСЛО ('+' ЧИСЛО)*
 */
public class Parser1 {
    /**
     * Список лексем
     */
    private final List<Token> tokens;
    /**
     * Индекс текущей лексемы
     */
    private int index = 0;
    public Parser1(List<Token> tokens) {
        this.tokens = tokens;
    }
    /**
     * Проверка типа текущей лексемы.
     *
     * @param type предполагаемый тип лексемы
     * @return не null, если текущая лексема предполагаемого типа (при этом
    текущи индекс сдвигается на 1);
     * null, если текущая лексема другого типа
     */
    private Token match(TokenType type) {
        if (index >= tokens.size())
            return null;
        Token token = tokens.get(index);
        if (token.getTType() != type)
            return null;
        index++;
        return token;
    }
    /**
     * Сообщение об ошибке с указанием текущей позиции в тексте.
     *
     * @param message текст сообщения
     */
    private void error(String message) throws ParseException {
        // Позиция ошибки в тексте
        int errorPosition;
        if (index >= tokens.size()) {
            // Мы стоим в конце текста
            if (tokens.isEmpty()) {
                // Лексем не было вообще - текст пустой; указываем на начало текста
                errorPosition = 0;
            } else {
                // Берем координату после последней лексемы
                errorPosition = tokens.get(tokens.size() - 1).getMatched();
            }
        } else {
            // Берем координату текущей лексемы
            Token token = tokens.get(index);
            errorPosition = token.getIndex();
        }
        throw new ParseException(message, errorPosition);
    }
    /**
     * Грамматический разбор выражения по грамматике
     *
     */
    public void matchExpression() throws ParseException {
        // В начале должно быть ЧИСЛО или ВЫРАЖЕНИЕ:
        Token n1 = match(TokenType.NUMBER);
        Token n2 = match(TokenType.LPAR);
        Token n3 = match(TokenType.RPAR);
        if ((!(n1 == null))&&(!(n2 == null))) {
            error("Missing number or exception");
        }
        while (true) {
            // Пока есть символ '+'|'-'...
            if ((match(TokenType.ADD) != null)||(match(TokenType.SUB) != null)) {
                // Требуем после плюса снова ЧИСЛО или ВЫРАЖЕНИЕ:
                Token n11 = match(TokenType.NUMBER);
                Token n12 = match(TokenType.LPAR);
                Token n13 = match(TokenType.RPAR);
                if (((n11 == null)&&(!(n12 == null))&&(!(n13 == null)))||((n11 == null)&&(n12 == null)&&(n13 == null))) {
                    error("Missing number or exception");
                }
            } else {
                break;
            }
        }
    }
    /**
     * Проверка грамматического разбора выражения
     */
    public static void main(String[] args) throws ParseException {
        String expression;
        System.out.println("Введите строку: ");
        Scanner in = new Scanner(System.in);
        expression = in.nextLine();
        Lexer lexer = new Lexer(expression);
        List<Token> allTokens = lexer.getAllTokens();
        Parser1 parser = new Parser1(allTokens);
        parser.matchExpression();
    }
}