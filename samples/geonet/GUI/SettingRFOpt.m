function varargout = SettingRFOpt(varargin)
%SETTINGRFOPT M-file for SettingRFOpt.fig
%      SETTINGRFOPT, by itself, creates a new SETTINGRFOPT or raises the existing
%      singleton*.
%
%      H = SETTINGRFOPT returns the handle to a new SETTINGRFOPT or the handle to
%      the existing singleton*.
%
%      SETTINGRFOPT('Property','Value',...) creates a new SETTINGRFOPT using the
%      given property value pairs. Unrecognized properties are passed via
%      varargin to SettingRFOpt_OpeningFcn.  This calling syntax produces a
%      warning when there is an existing singleton*.
%
%      SETTINGRFOPT('CALLBACK') and SETTINGRFOPT('CALLBACK',hObject,...) call the
%      local function named CALLBACK in SETTINGRFOPT.M with the given input
%      arguments.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help SettingRFOpt

% Last Modified by GUIDE v2.5 05-Aug-2014 11:47:34

%----------------------------------------------------------------------------------------------
%                           GeoNet v0.1.2 beta
%
% Copyright (c) 2014-2014 Lorenzo Rossi, Daniele Sampietro
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
                   'gui_OpeningFcn', @SettingRFOpt_OpeningFcn, ...
                   'gui_OutputFcn',  @SettingRFOpt_OutputFcn, ...
                   'gui_LayoutFcn',  [], ...
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


% --- Executes just before SettingRFOpt is made visible.
function SettingRFOpt_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   unrecognized PropertyName/PropertyValue pairs from the
%            command line (see VARARGIN)

% Choose default command line output for SettingRFOpt
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes SettingRFOpt wait for user response (see UIRESUME)
% uiwait(handles.figure1);
global geonetGUI
geonetGUI.OpenSetRFOption(handles);

% --- Outputs from this function are returned to the command line.
function varargout = SettingRFOpt_OutputFcn(hObject, eventdata, handles)
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on selection change in popEllipsoid.
function popEllipsoid_Callback(hObject, eventdata, handles)
% hObject    handle to popEllipsoid (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popEllipsoid contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popEllipsoid


% --- Executes during object creation, after setting all properties.
function popEllipsoid_CreateFcn(hObject, eventdata, handles)
% hObject    handle to popEllipsoid (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in bOk.
function bOk_Callback(hObject, eventdata, handles)
% hObject    handle to bOk (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.lockHome;
geonetGUI.setRFoption;


% --- Executes on button press in bCancel.
function bCancel_Callback(hObject, eventdata, handles)
% hObject    handle to bCancel (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
close(geonetGUI.oh.fRFsetting)


% --- Executes on selection change in popGeoid.
function popGeoid_Callback(hObject, eventdata, handles)
% hObject    handle to popGeoid (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns popGeoid contents as cell array
%        contents{get(hObject,'Value')} returns selected item from popGeoid


% --- Executes during object creation, after setting all properties.
function popGeoid_CreateFcn(hObject, eventdata, handles)
% hObject    handle to popGeoid (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes when user attempts to close fRFsetting.
function fRFsetting_CloseRequestFcn(hObject, eventdata, handles)
% hObject    handle to fRFsetting (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: delete(hObject) closes the figure
delete(hObject);
global geonetGUI
geonetGUI.unlockHome;
