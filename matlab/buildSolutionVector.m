function [ v ] = buildSolutionVector( subRho, useVector )
%BUILDSOLUTIONVECTOR Summary of this function goes here
%   Detailed explanation goes here

sizeUseVector = size(useVector,1);
sizeSubRho = size(subRho,1);

index = transpose(1:sizeUseVector);

useVectorCum = cumsum(useVector);

Z = zeros(sizeSubRho,sizeUseVector);

Z(((index(useVector == 1) - 1)*sizeSubRho) + useVectorCum(useVector == 1)) = 1;

v = transpose(Z) * subRho;

v(sizeUseVector,1) = 1;

end

