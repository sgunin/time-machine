function varargout = AdjustmentOpt(varargin)
% ADJUSTMENTOPT MATLAB code for AdjustmentOpt.fig
%      ADJUSTMENTOPT, by itself, creates a new ADJUSTMENTOPT or raises the existing
%      singleton*.
%
%      H = ADJUSTMENTOPT returns the handle to a new ADJUSTMENTOPT or the handle to
%      the existing singleton*.
%
%      ADJUSTMENTOPT('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in ADJUSTMENTOPT.M with the given input arguments.
%
%      ADJUSTMENTOPT('Property','Value',...) creates a new ADJUSTMENTOPT or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before AdjustmentOpt_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to AdjustmentOpt_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help AdjustmentOpt

% Last Modified by GUIDE v2.5 06-Aug-2014 11:08:59

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
                   'gui_OpeningFcn', @AdjustmentOpt_OpeningFcn, ...
                   'gui_OutputFcn',  @AdjustmentOpt_OutputFcn, ...
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


% --- Executes just before AdjustmentOpt is made visible.
function AdjustmentOpt_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to AdjustmentOpt (see VARARGIN)

% Choose default command line output for AdjustmentOpt
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes AdjustmentOpt wait for user response (see UIRESUME)
% uiwait(handles.fAdjOpt);
global geonetGUI
geonetGUI.lockHome
geonetGUI.openAdjOpt(handles);

% --- Outputs from this function are returned to the command line.
function varargout = AdjustmentOpt_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on button press in pushbutton2.
function pushbutton2_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
close(geonetGUI.oh.fAdjOpt);


% --- Executes on button press in pushbutton3.
function pushbutton3_Callback(hObject, eventdata, handles)
% hObject    handle to pushbutton3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.launchAdj

% --- Executes on button press in cBessel.
function cBessel_Callback(hObject, eventdata, handles)
% hObject    handle to cBessel (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of cBessel


% --- Executes on button press in cAtmo.
function cAtmo_Callback(hObject, eventdata, handles)
% hObject    handle to cAtmo (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: get(hObject,'Value') returns toggle state of cAtmo



function edit1_Callback(hObject, eventdata, handles)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of edit1 as text
%        str2double(get(hObject,'String')) returns contents of edit1 as a double


% --- Executes during object creation, after setting all properties.
function edit1_CreateFcn(hObject, eventdata, handles)
% hObject    handle to edit1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function eGPSw_Callback(hObject, eventdata, handles)
% hObject    handle to eGPSw (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of eGPSw as text
%        str2double(get(hObject,'String')) returns contents of eGPSw as a double
if isnan(str2double(get(hObject,'String'))) || str2double(get(hObject,'String')) <= 0
    global geonetGUI
    set(hObject,'String',geonetGUI.LSoptions.GPSw)
    msgbox('The value must be a number greater than 0','Error','error');
end

% --- Executes during object creation, after setting all properties.
function eGPSw_CreateFcn(hObject, eventdata, handles)
% hObject    handle to eGPSw (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function eAlfa_Callback(hObject, eventdata, handles)
% hObject    handle to eAlfa (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of eAlfa as text
%        str2double(get(hObject,'String')) returns contents of eAlfa as a double
if isnan(str2double(get(hObject,'String'))) || str2double(get(hObject,'String')) <= 0 || str2double(get(hObject,'String')) >= 100
    global geonetGUI
    set(hObject,'String',geonetGUI.LSoptions.alfa)
    msgbox('The value must be a number between 0 and 100','Error','error');
end

% --- Executes during object creation, after setting all properties.
function eAlfa_CreateFcn(hObject, eventdata, handles)
% hObject    handle to eAlfa (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function eBeta_Callback(hObject, eventdata, handles)
% hObject    handle to eBeta (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of eBeta as text
%        str2double(get(hObject,'String')) returns contents of eBeta as a double
if isnan(str2double(get(hObject,'String'))) || str2double(get(hObject,'String')) <= 0 || str2double(get(hObject,'String')) >= 100
    global geonetGUI
    set(hObject,'String',geonetGUI.LSoptions.beta)
    msgbox('The value must be a number between 0 and 100','Error','error');
end

% --- Executes during object creation, after setting all properties.
function eBeta_CreateFcn(hObject, eventdata, handles)
% hObject    handle to eBeta (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function eImax_Callback(hObject, eventdata, handles)
% hObject    handle to eImax (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of eImax as text
%        str2double(get(hObject,'String')) returns contents of eImax as a double
if isnan(str2double(get(hObject,'String'))) || str2double(get(hObject,'String')) <= 0
    global geonetGUI
    set(hObject,'String',geonetGUI.LSoptions.imax)
    msgbox('The value must be a number','Error','error');
end

% --- Executes during object creation, after setting all properties.
function eImax_CreateFcn(hObject, eventdata, handles)
% hObject    handle to eImax (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes when user attempts to close fAdjOpt.
function fAdjOpt_CloseRequestFcn(hObject, eventdata, handles)
% hObject    handle to fAdjOpt (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: delete(hObject) closes the figure
delete(hObject);
global geonetGUI
geonetGUI.unlockHome
