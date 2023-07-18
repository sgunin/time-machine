function varargout = ImportCoord(varargin)
% FIMPORTCOORD MATLAB code for fImportCoord.fig
%      FIMPORTCOORD, by itself, creates a new FIMPORTCOORD or raises the existing
%      singleton*.
%
%      H = FIMPORTCOORD returns the handle to a new FIMPORTCOORD or the handle to
%      the existing singleton*.
%
%      FIMPORTCOORD('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in FIMPORTCOORD.M with the given input arguments.
%
%      FIMPORTCOORD('Property','Value',...) creates a new FIMPORTCOORD or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before ImportCoord_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to ImportCoord_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help fImportCoord

% Last Modified by GUIDE v2.5 05-Aug-2014 11:36:06

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
                   'gui_OpeningFcn', @ImportCoord_OpeningFcn, ...
                   'gui_OutputFcn',  @ImportCoord_OutputFcn, ...
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


% --- Executes just before fImportCoord is made visible.
function ImportCoord_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to fImportCoord (see VARARGIN)

% Choose default command line output for fImportCoord
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes fImportCoord wait for user response (see UIRESUME)
% uiwait(handles.fImportCoord);
global geonetGUI
geonetGUI.OpenImportCoord(handles);


% --- Outputs from this function are returned to the command line.
function varargout = ImportCoord_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on button press in bOK.
function bOK_Callback(hObject, eventdata, handles)
% hObject    handle to bOK (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
switch geonetGUI.CoordImpType
    case 1
        geonetGUI.importCoordApp
        geonetGUI.unlockHome;
    case 2
        geonetGUI.importConstraints
        geonetGUI.unlockConstraints;
end


% --- Executes on button press in bCancel.
function bCancel_Callback(hObject, eventdata, handles)
% hObject    handle to bCancel (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
close(handles.fImportCoord);

% --- Executes on selection change in pDelimiter.
function pDelimiter_Callback(hObject, eventdata, handles)
% hObject    handle to pDelimiter (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns pDelimiter contents as cell array
%        contents{get(hObject,'Value')} returns selected item from pDelimiter


% --- Executes during object creation, after setting all properties.
function pDelimiter_CreateFcn(hObject, eventdata, handles)
% hObject    handle to pDelimiter (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function eHeader_Callback(hObject, eventdata, handles)
% hObject    handle to eHeader (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of eHeader as text
%        str2double(get(hObject,'String')) returns contents of eHeader as a double


% --- Executes during object creation, after setting all properties.
function eHeader_CreateFcn(hObject, eventdata, handles)
% hObject    handle to eHeader (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
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
ftype = 1;                    % set file type to 1 (txt file) - during coordinates the file type isn't chosen and so it must me set here
geonetGUI.getFileName(ftype); % lunch the file searcher and get file name

% --- Executes on selection change in field2.
function field2_Callback(hObject, eventdata, handles)
% hObject    handle to field2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns field2 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from field2


% --- Executes during object creation, after setting all properties.
function field2_CreateFcn(hObject, eventdata, handles)
% hObject    handle to field2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in field3.
function field3_Callback(hObject, eventdata, handles)
% hObject    handle to field3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns field3 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from field3


% --- Executes during object creation, after setting all properties.
function field3_CreateFcn(hObject, eventdata, handles)
% hObject    handle to field3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in field4.
function field4_Callback(hObject, eventdata, handles)
% hObject    handle to field4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns field4 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from field4


% --- Executes during object creation, after setting all properties.
function field4_CreateFcn(hObject, eventdata, handles)
% hObject    handle to field4 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in field5.
function field5_Callback(hObject, eventdata, handles)
% hObject    handle to field5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns field5 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from field5


% --- Executes during object creation, after setting all properties.
function field5_CreateFcn(hObject, eventdata, handles)
% hObject    handle to field5 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in field1.
function field1_Callback(hObject, eventdata, handles)
% hObject    handle to field1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns field1 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from field1


% --- Executes during object creation, after setting all properties.
function field1_CreateFcn(hObject, eventdata, handles)
% hObject    handle to field1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes when selected object is changed in pCoordType.
function pCoordType_SelectionChangeFcn(hObject, eventdata, handles)
% hObject    handle to the selected object in pCoordType 
% eventdata  structure with the following fields (see UIBUTTONGROUP)
%	EventName: string 'SelectionChanged' (read only)
%	OldValue: handle of the previously selected object or empty if none was selected
%	NewValue: handle of the currently selected object
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.setCoordtype


% --- Executes when user attempts to close fImportCoord.
function fImportCoord_CloseRequestFcn(hObject, eventdata, handles)
% hObject    handle to fImportCoord (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: delete(hObject) closes the figure
delete(hObject);
global geonetGUI
switch geonetGUI.CoordImpType 
    case 1
        geonetGUI.unlockHome;
    case 2
        geonetGUI.unlockConstraints;
end