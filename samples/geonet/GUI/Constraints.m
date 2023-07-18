function varargout = Constraints(varargin)
% CONSTRAINTS MATLAB code for Constraints.fig
%      CONSTRAINTS, by itself, creates a new CONSTRAINTS or raises the existing
%      singleton*.
%
%      H = CONSTRAINTS returns the handle to a new CONSTRAINTS or the handle to
%      the existing singleton*.
%
%      CONSTRAINTS('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in CONSTRAINTS.M with the given input arguments.
%
%      CONSTRAINTS('Property','Value',...) creates a new CONSTRAINTS or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before Constraints_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to Constraints_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help Constraints

% Last Modified by GUIDE v2.5 06-Aug-2014 09:49:46

%----------------------------------------------------------------------------------------------
%                           GeoNet v0.1.2 beta
%
% Copyright (c) 2014 Lorenzo Rossi, Daniele Sampietro
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

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @Constraints_OpeningFcn, ...
                   'gui_OutputFcn',  @Constraints_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before Constraints is made visible.
function Constraints_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to Constraints (see VARARGIN)

% Choose default command line output for Constraints
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes Constraints wait for user response (see UIRESUME)
% uiwait(handles.fConstraints);
global geonetGUI
geonetGUI.lockHome;
geonetGUI.openConstraintsUI(handles);

% --- Outputs from this function are returned to the command line.
function varargout = Constraints_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on button press in bRemove.
function bRemove_Callback(hObject, eventdata, handles)
% hObject    handle to bRemove (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.removeAllConstraints


% --- Executes on button press in bImport.
function bImport_Callback(hObject, eventdata, handles)
% hObject    handle to bImport (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.CoordImpType = 2;
ImportCoord;

% --- Executes when selected object is changed in pCoord.
function pCoord_SelectionChangeFcn(hObject, eventdata, handles)
% hObject    handle to the selected object in pCoord 
% eventdata  structure with the following fields (see UIBUTTONGROUP)
%	EventName: string 'SelectionChanged' (read only)
%	OldValue: handle of the previously selected object or empty if none was selected
%	NewValue: handle of the currently selected object
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.setTableConstraints


% --- Executes when user attempts to close fConstraints.
function fConstraints_CloseRequestFcn(hObject, eventdata, handles)
% hObject    handle to fConstraints (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: delete(hObject) closes the figure
delete(hObject);
global geonetGUI
geonetGUI.unlockHome;
