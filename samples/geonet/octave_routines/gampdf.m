function pdf = gampdf (x, a, b)
% For each element of x, return the probability density function
% (PDF) at x of the Gamma distribution with parameters a
% and b.

%----------------------------------------------------------------------------------------------
%                           GeoNet v0.1.2 beta
%
% Copyright (C) 1995-2011 Kurt Hornik
% Copyright (C) 2014 Lorenzo Rossi, Daniele Sampietro

% Adapted from Octave.
% Author: TT <Teresa.Twaroch@ci.tuwien.ac.at>
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
      error ('gampdf: X, A and B must be of common size or scalars');
    end
  end

  sz = size(x);
  pdf = zeros (sz);

  k = find (~(a > 0) | ~(b > 0) | isnan (x));
  if (any (k))
    pdf (k) = NaN;
  end

  k = find ((x > 0) & (a > 0) & (a <= 1) & (b > 0));
  if (any (k))
    if (isscalar(a) && isscalar(b))
      pdf(k) = (x(k) .^ (a - 1)) ...
                .* exp(- x(k) ./ b) ./ gamma (a) ./ (b .^ a);
    else
      pdf(k) = (x(k) .^ (a(k) - 1)) ...
                .* exp(- x(k) ./ b(k)) ./ gamma (a(k)) ./ (b(k) .^ a(k));
    end
  end

  k = find ((x > 0) & (a > 1) & (b > 0));
  if (any (k))
    if (isscalar(a) && isscalar(b))
      pdf(k) = exp (- a .* log (b) + (a-1) .* log (x(k)) ...
                    - x(k) ./ b - gammaln (a));
    else
      pdf(k) = exp (- a(k) .* log (b(k)) + (a(k)-1) .* log (x(k)) ...
                    - x(k) ./ b(k) - gammaln (a(k)));
    end
  end

end
