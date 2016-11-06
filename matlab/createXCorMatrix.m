function [ X ] = createXCorMatrix( n, k )
%CREATEXCORMATRIX Summary of this function goes here
%   Detailed explanation goes here

X = transpose(0:(k-1));
for i = 2:n 
    s = size(X,1);
    B = zeros(0,size(X,2)+1);
    for j = 0:(k-1)
        C = [j*ones(s,1) X];
        B = [B; C];
    end
    X = B;
end

end

