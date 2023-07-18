function inv = gaminv (x, a, b)
% For each component of x, compute the quantile (the inverse of
% the CDF) at x of the Gamma distribution with parameters a
% and b.

%----------------------------------------------------------------------------------------------
%                           GeoNet v0.1.2 beta
%
% Copyright (C) 1995-2011 Kurt Hornik
% Copyright (C) 2014 Lorenzo Rossi, Daniele Sampietro

% Adapted from Octave.
% Author: KH <Kurt.Hornik@wu-wien.ac.at>
%----------------------------------------------------------------------------------------------
%
%    This program is free software: you can redistribute it and/or modify
%    it under the terms of the GNU General Public License as published by
%    the Free Software Foundation, either version 3 of the License, or
%    (at your option) any later version.
%
%    This program is distributed in the hope that it will be useful,
%    but WITHOUT ANY WARRANTY; without even the implied warranty of
%    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
%    GNU General Public License for more details.
%
%    You should have received a copy of the GNU General Public License
%    along with this program.  If not, see <http://www.gnu.org/licenses/>.
%----------------------------------------------------------------------------------------------


if (~isscalar (a) || ~isscalar(b))
    [retval, x, a, b] = common_size (x, a, b);
    if (retval > 0)
        error ('gaminv: X, A and B must be of common size or scalars');
    end
end

sz = size (x);
inv = zeros (sz);

k = find ((x < 0) | (x > 1) | isnan (x) | ~(a > 0) | ~(b > 0));
if (any (k))
    inv (k) = NaN;
end

k = find ((x == 1) & (a > 0) & (b > 0));
if (any (k))
    inv (k) = Inf;
end

k = find ((x > 0) & (x < 1) & (a > 0) & (b > 0));
if (any (k))
    if (~isscalar(a) || ~isscalar(b))
        a = a (k);
        b = b (k);
        y = a .* b;
    else
        y = a * b * ones (size (k));
    end
    x = x (k);
    
    if (isa (x, 'single'))
        myeps = eps ('single');
    else
        myeps = eps;
    end
    
    l = find (x < myeps);
    if (any (l))
        y(l) = sqrt (myeps) * ones (length (l), 1);
    end
    
    y_old = y;
    for i = 1 : 100
        h     = (gamcdf (y_old, a, b) - x) ./ gampdf (y_old, a, b);
        y_new = y_old - h;
        ind   = find (y_new <= myeps);
        if (any (ind))
            y_new (ind) = y_old (ind) / 10;
            h = y_old - y_new;
        end
        if (max (abs (h)) < sqrt (myeps))
            break;
        end
        y_old = y_new;
    end
    
    inv (k) = y_new;
end

end
