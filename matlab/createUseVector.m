function [ I ] = createUseVector( X )
%CREATEUSEVECTOR Summary of this function goes here
%   Detailed explanation goes here

maxValue = max(max(X));
I = sum(X == maxValue,2) > 0;

l = size(I,1);
I(l,1) = 0;

end

