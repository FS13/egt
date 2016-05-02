function [ Pi ] = computeEdge( i, j, w, k, X, f, Pi )
%COMPUTEEDGE Summary of this function goes here
%   Detailed explanation goes here

n = size(X,2);

I = eye(n);
I(j,j) = 0;
I(i,j) = 1;

J = zeros(n,1);
J(i,1) = w;

Y = X*I;
F = f*J;

Yhat = zeros(size(Y,1),1);
for l = 1:n
    Yhat = Yhat + Y(:,l) * k^(n-l);
end
for l = 1:size(Y,1)
    Pi(l,Yhat(l)+1) = Pi(l,Yhat(l)+1) + F(l);
end

end

