package com.example.dsdraw.structures;

public class Node
{
//    int key;
    public char label;
    public Node left, right;

//    public Node(int item)
//    {
//        key = item;
//        left = right = null;
//    }

    public Node(char _label)
    {
        label = _label;
        left = right = null;
    }
}
