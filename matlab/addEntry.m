function [ X ] = addEntry(A,m,n,p)
%ADDENTRY Summary of this function goes here
%   Detailed explanation goes here

A(m,n) = A(m,n) + p;
X = A;
end

