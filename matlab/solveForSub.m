function [ v ] = solveForSub( A, b )
%SOLVEFORSUB Summary of this function goes here
%   Detailed explanation goes here

rows = size(A,1);
columns = size(A,2);

I = eye(rows,columns);

v = mldivide(I - A, b);
end

