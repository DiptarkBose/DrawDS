package com.example.dsdraw.structures;

public class BinaryTree
{
    public Node root;

    BinaryTree(char ch)
    {
        root = new Node(ch);
    }

    public BinaryTree()
    {
        root = null;
    }

    public static BinaryTree getBasicTree() {
        BinaryTree tree = new BinaryTree();
        tree.root = new Node('A');
        tree.root.left = new Node('B');
        tree.root.right = new Node('C');
        tree.root.left.left = new Node('D');
        return tree;
    }
}
