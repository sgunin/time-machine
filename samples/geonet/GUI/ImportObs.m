function varargout = ImportObs(varargin)
%IMPORTOBS M-file for ImportObs.fig
%      IMPORTOBS, by itself, creates a new IMPORTOBS or raises the existing
%      singleton*.
%
%      H = IMPORTOBS returns the handle to a new IMPORTOBS or the handle to
%      the existing singleton*.
%
%      IMPORTOBS('Property','Value',...) creates a new IMPORTOBS using the
%      given property value pairs. Unrecognized properties are passed via
%      varargin to ImportObs_OpeningFcn.  This calling syntax produces a
%      warning when there is an existing singleton*.
%
%      IMPORTOBS('CALLBACK') and IMPORTOBS('CALLBACK',hObject,...) call the
%      local function named CALLBACK in IMPORTOBS.M with the given input
%      arguments.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help ImportObs

% Last Modified by GUIDE v2.5 05-Aug-2014 11:34:35

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
                   'gui_OpeningFcn', @ImportObs_OpeningFcn, ...
                   'gui_OutputFcn',  @ImportObs_OutputFcn, ...
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


% --- Executes just before ImportObs is made visible.
function ImportObs_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   unrecognized PropertyName/PropertyValue pairs from the
%            command line (see VARARGIN)
global geonetGUI
geonetGUI.lockHome;
% Choose default command line output for ImportObs
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);


switch geonetGUI.ObsType % Understand wich type of observation we are importing
    case 1     % Total station
        geonetGUI.OpenImportTSobs(handles);
    case 2     % Levelling
        geonetGUI.OpenImportLEVobs(handles);
    case 3     % GPS single point
        geonetGUI.OpenImportGPSobs(handles);
    case 4     % Baseline
        geonetGUI.OpenImportBLobs(handles);
end
% UIWAIT makes ImportObs wait for user response (see UIRESUME)
% uiwait(handles.ImportObs);


% --- Outputs from this function are returned to the command line.
function varargout = ImportObs_OutputFcn(hObject, eventdata, handles)
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on selection change in pmFileType.
function pmFileType_Callback(hObject, eventdata, handles)
% hObject    handle to pmFileType (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
% Hints: contents = cellstr(get(hObject,'String')) returns pmFileType contents as cell array
%        contents{get(hObject,'Value')} returns selected item from pmFileType

geonetGUI.setfiletype; % operations after selecting file type

% --- Executes during object creation, after setting all properties.
function pmFileType_CreateFcn(hObject, eventdata, handles)
% hObject    handle to pmFileType (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function tFilePath_Callback(hObject, eventdata, handles)
% hObject    handle to tFilePath (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tFilePath as text
%        str2double(get(hObject,'String')) returns contents of tFilePath as a double


% --- Executes during object creation, after setting all properties.
function tFilePath_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tFilePath (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in bSelectPath.
function bSelectPath_Callback(hObject, eventdata, handles)
% hObject    handle to bSelectPath (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
ftype = get(geonetGUI.ih.pmFileType,'Value') - 1;    % Get the row of the selected file type (-1 is because first row is select file type)
geonetGUI.getFileName(ftype);                        % lunch the file searcher and get file name




% --- Executes on button press in bImport.
function bImport_Callback(hObject, eventdata, handles)
% hObject    handle to bImport (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% buttn next used to import data
global geonetGUI 
switch geonetGUI.ObsType % Understand wich type of observation we are importing
    case 1     % Total station
        geonetGUI.importTSdata;
    case 2     % Levelling
        geonetGUI.importLEVdata;
    case 3     % GPS single point
        geonetGUI.importGPSdata;
    case 4     % Baseline
        geonetGUI.importBLdata;
end
geonetGUI.unlockHome;


% --- Executes on button press in bCancel.
function bCancel_Callback(hObject, eventdata, handles)
% hObject    handle to bCancel (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
close(handles.ImportObs);


% --- Executes on button press in bOptions.
function bOptions_Callback(hObject, eventdata, handles)
% hObject    handle to bOptions (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
TxtOption % open the txt option mask




function eSigmaDh_Callback(hObject, eventdata, handles)
% hObject    handle to eSigmaDh (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of eSigmaDh as text
%        str2double(get(hObject,'String')) returns contents of eSigmaDh as a double


% --- Executes during object creation, after setting all properties.
function eSigmaDh_CreateFcn(hObject, eventdata, handles)
% hObject    handle to eSigmaDh (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function eSigmaD_Callback(hObject, eventdata, handles)
% hObject    handle to eSigmaD (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of eSigmaD as text
%        str2double(get(hObject,'String')) returns contents of eSigmaD as a double


% --- Executes during object creation, after setting all properties.
function eSigmaD_CreateFcn(hObject, eventdata, handles)
% hObject    handle to eSigmaD (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function ePpmD_Callback(hObject, eventdata, handles)
% hObject    handle to ePpmD (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of ePpmD as text
%        str2double(get(hObject,'String')) returns contents of ePpmD as a double


% --- Executes during object creation, after setting all properties.
function ePpmD_CreateFcn(hObject, eventdata, handles)
% hObject    handle to ePpmD (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function eSigmaDnp_Callback(hObject, eventdata, handles)
% hObject    handle to eSigmaDnp (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of eSigmaDnp as text
%        str2double(get(hObject,'String')) returns contents of eSigmaDnp as a double


% --- Executes during object creation, after setting all properties.
function eSigmaDnp_CreateFcn(hObject, eventdata, handles)
% hObject    handle to eSigmaDnp (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function eDnpPpm_Callback(hObject, eventdata, handles)
% hObject    handle to eDnpPpm (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of eDnpPpm as text
%        str2double(get(hObject,'String')) returns contents of eDnpPpm as a double


% --- Executes during object creation, after setting all properties.
function eDnpPpm_CreateFcn(hObject, eventdata, handles)
% hObject    handle to eDnpPpm (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function eSigmaA_Callback(hObject, eventdata, handles)
% hObject    handle to eSigmaA (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of eSigmaA as text
%        str2double(get(hObject,'String')) returns contents of eSigmaA as a double


% --- Executes during object creation, after setting all properties.
function eSigmaA_CreateFcn(hObject, eventdata, handles)
% hObject    handle to eSigmaA (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function eSigmaZ_Callback(hObject, eventdata, handles)
% hObject    handle to eSigmaZ (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of eSigmaZ as text
%        str2double(get(hObject,'String')) returns contents of eSigmaZ as a double


% --- Executes during object creation, after setting all properties.
function eSigmaZ_CreateFcn(hObject, eventdata, handles)
% hObject    handle to eSigmaZ (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function ePpmDh_Callback(hObject, eventdata, handles)
% hObject    handle to ePpmDh (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of ePpmDh as text
%        str2double(get(hObject,'String')) returns contents of ePpmDh as a double


% --- Executes during object creation, after setting all properties.
function ePpmDh_CreateFcn(hObject, eventdata, handles)
% hObject    handle to ePpmDh (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function eSigmaHs_Callback(hObject, eventdata, handles)
% hObject    handle to eSigmaHs (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of eSigmaHs as text
%        str2double(get(hObject,'String')) returns contents of eSigmaHs as a double


% --- Executes during object creation, after setting all properties.
function eSigmaHs_CreateFcn(hObject, eventdata, handles)
% hObject    handle to eSigmaHs (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes when user attempts to close ImportObs.
function ImportObs_CloseRequestFcn(hObject, eventdata, handles)
% hObject    handle to ImportObs (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: delete(hObject) closes the figure
delete(hObject);
global geonetGUI
geonetGUI.unlockHome;
