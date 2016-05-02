function [ v ] = createSubVector( A, use )
%CREATESUBVECTOR Summary of this function goes here
%   Detailed explanation goes here

columns = size(A,2);

v = A(use == 1,columns);


end

