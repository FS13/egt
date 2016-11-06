function [ indices ] = getIndices( state )
%GETINDICES Summary of this function goes here
%   Detailed explanation goes here

k = size(state, 1);
n = sum(state);

species = zeros(0,1);
for i = 2:k
    species = [species ; ones(state(i,1),1)*(i-2)];
end
species = [species ; ones(state(1,1),1)*(k-1)];

states = perms(species);

indices = zeros(size(states,1),1);
for l = 1:n
    indices = indices + states(:,l) * k^(n-l);
end

indices = unique(indices);

end

