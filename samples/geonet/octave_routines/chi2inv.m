function inv = chi2inv (x, n)
% For each element of x, compute the quantile (the inverse of the
% CDF) at x of the chisquare distribution with n degrees of
% freedom.

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

if (~isscalar (n))
    [retval, x, n] = common_size (x, n);
    if (retval > 0)
        error ('chi2inv: X and N must be of common size or scalar');
    end
end

inv = gaminv (x, n / 2, 2);

end
