function inv = stdnormal_inv (x)
% For each component of x, compute the quantile (the
% inverse of the CDF) at x of the standard normal distribution.

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

  inv = sqrt (2) * erfinv (2 * x - 1);

end
