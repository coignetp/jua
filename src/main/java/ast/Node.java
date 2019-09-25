package ast;

import token.Token;

import java.util.ArrayList;

public class Node {
    private Token token;

    private ArrayList<Node> children = new ArrayList<>();


    public Node(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }

    public void addChild(Node node) {
        children.add(node);
    }

    public ArrayList<Node> getChildren() {
        return children;
    }
}
