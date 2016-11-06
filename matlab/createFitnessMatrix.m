function [ f ] = createFitnessMatrix( X, r )
%CREATEFITNESSMATRIX Summary of this function goes here
%   Detailed explanation goes here

f = r(X+1);
f = diag(sum(f,2).^(-1))*f;

end