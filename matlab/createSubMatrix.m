function [ X ] = createSubMatrix( A, use )
%CREATESUBMATRICES Summary of this function goes here
%   Detailed explanation goes here

X = A(use == 1,use ==1);

end

