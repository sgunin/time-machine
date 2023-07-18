function inv = norminv (x, m, s)
% For each element of x, compute the quantile (the inverse of the
% CDF) at x of the normal distribution with mean m and
% standard deviation s.

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

  if (nargin == 1)
    m = 0;
    s = 1;
  end

  if (~isscalar (m) || ~isscalar (s))
    [retval, x, m, s] = common_size (x, m, s);
    if (retval > 0)
      error ('norminv: X, M and S must be of common size or scalars');
    end
  end

  sz = size (x);
  inv = zeros (sz);

  if (isscalar (m) && isscalar (s))
    if (find (isinf (m) | isnan (m) | ~(s > 0) | ~(s < Inf)))
      inv = NaN (sz);
    else
      inv =  m + s .* stdnormal_inv (x);
    end
  else
    k = find (isinf (m) | isnan (m) | ~(s > 0) | ~(s < Inf));
    if (any (k))
      inv(k) = NaN;
    end

    k = find (~isinf (m) & ~isnan (m) & (s > 0) & (s < Inf));
    if (any (k))
      inv(k) = m(k) + s(k) .* stdnormal_inv (x(k));
    end
  end

  k = find ((s == 0) & (x > 0) & (x < 1));
  if (any (k))
    inv(k) = m(k);
  end

  inv((s == 0) & (x == 0)) = -Inf;
  inv((s == 0) & (x == 1)) = Inf;

end
