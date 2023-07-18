function varargout = TxtOption(varargin)
%TXTOPTION M-file for TxtOption.fig
%      TXTOPTION, by itself, creates a new TXTOPTION or raises the existing
%      singleton*.
%
%      H = TXTOPTION returns the handle to a new TXTOPTION or the handle to
%      the existing singleton*.
%
%      TXTOPTION('Property','Value',...) creates a new TXTOPTION using the
%      given property value pairs. Unrecognized properties are passed via
%      varargin to TxtOption_OpeningFcn.  This calling syntax produces a
%      warning when there is an existing singleton*.
%
%      TXTOPTION('CALLBACK') and TXTOPTION('CALLBACK',hObject,...) call the
%      local function named CALLBACK in TXTOPTION.M with the given input
%      arguments.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help TxtOption

% Last Modified by GUIDE v2.5 05-Aug-2014 11:45:04

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
                   'gui_OpeningFcn', @TxtOption_OpeningFcn, ...
                   'gui_OutputFcn',  @TxtOption_OutputFcn, ...
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


% --- Executes just before TxtOption is made visible.
function TxtOption_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   unrecognized PropertyName/PropertyValue pairs from the
%            command line (see VARARGIN)
global geonetGUI;
geonetGUI.lockImport;
geonetGUI.openTXToption(handles);

% Choose default command line output for TxtOption
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes TxtOption wait for user response (see UIRESUME)
% uiwait(handles.fTXToption);


% --- Outputs from this function are returned to the command line.
function varargout = TxtOption_OutputFcn(hObject, eventdata, handles)
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
geonetGUI.closeTXToption

% --- Executes on button press in bCancel.
function bCancel_Callback(hObject, eventdata, handles)
% hObject    handle to bCancel (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
close(geonetGUI.oh.fTXToption);

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



function ePersonalDelimiter_Callback(hObject, eventdata, handles)
% hObject    handle to ePersonalDelimiter (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of ePersonalDelimiter as text
%        str2double(get(hObject,'String')) returns contents of ePersonalDelimiter as a double


% --- Executes during object creation, after setting all properties.
function ePersonalDelimiter_CreateFcn(hObject, eventdata, handles)
% hObject    handle to ePersonalDelimiter (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


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


% --- Executes on selection change in field6.
function field6_Callback(hObject, eventdata, handles)
% hObject    handle to field6 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns field6 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from field6


% --- Executes during object creation, after setting all properties.
function field6_CreateFcn(hObject, eventdata, handles)
% hObject    handle to field6 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in field7.
function field7_Callback(hObject, eventdata, handles)
% hObject    handle to field7 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns field7 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from field7


% --- Executes during object creation, after setting all properties.
function field7_CreateFcn(hObject, eventdata, handles)
% hObject    handle to field7 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in field8.
function field8_Callback(hObject, eventdata, handles)
% hObject    handle to field8 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns field8 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from field8


% --- Executes during object creation, after setting all properties.
function field8_CreateFcn(hObject, eventdata, handles)
% hObject    handle to field8 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in field9.
function field9_Callback(hObject, eventdata, handles)
% hObject    handle to field9 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns field9 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from field9


% --- Executes during object creation, after setting all properties.
function field9_CreateFcn(hObject, eventdata, handles)
% hObject    handle to field9 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in field10.
function field10_Callback(hObject, eventdata, handles)
% hObject    handle to field10 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns field10 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from field10


% --- Executes during object creation, after setting all properties.
function field10_CreateFcn(hObject, eventdata, handles)
% hObject    handle to field10 (see GCBO)
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


% --- Executes on selection change in field11.
function field11_Callback(hObject, eventdata, handles)
% hObject    handle to field11 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns field11 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from field11


% --- Executes during object creation, after setting all properties.
function field11_CreateFcn(hObject, eventdata, handles)
% hObject    handle to field11 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in field12.
function field12_Callback(hObject, eventdata, handles)
% hObject    handle to field12 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns field12 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from field12


% --- Executes during object creation, after setting all properties.
function field12_CreateFcn(hObject, eventdata, handles)
% hObject    handle to field12 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on selection change in field13.
function field13_Callback(hObject, eventdata, handles)
% hObject    handle to field13 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = cellstr(get(hObject,'String')) returns field13 contents as cell array
%        contents{get(hObject,'Value')} returns selected item from field13


% --- Executes during object creation, after setting all properties.
function field13_CreateFcn(hObject, eventdata, handles)
% hObject    handle to field13 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes when user attempts to close fTXToption.
function fTXToption_CloseRequestFcn(hObject, eventdata, handles)
% hObject    handle to fTXToption (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: delete(hObject) closes the figure
global geonetGUI
geonetGUI.unlockImport;
delete(hObject);
