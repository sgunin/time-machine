function varargout = GeoNet(varargin)
%GEONET M-file for GeoNet.fig
%      GEONET, by itself, creates a new GEONET or raises the existing
%      singleton*.
%
%      H = GEONET returns the handle to a new GEONET or the handle to
%      the existing singleton*.
%
%      GEONET('Property','Value',...) creates a new GEONET using the
%      given property value pairs. Unrecognized properties are passed via
%      varargin to GeoNet_OpeningFcn.  This calling syntax produces a
%      warning when there is an existing singleton*.
%
%      GEONET('CALLBACK') and GEONET('CALLBACK',hObject,...) call the
%      local function named CALLBACK in GEONET.M with the given input
%      arguments.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help GeoNet

% Last Modified by GUIDE v2.5 22-Nov-2015 21:42:56

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
                   'gui_OpeningFcn', @GeoNet_OpeningFcn, ...
                   'gui_OutputFcn',  @GeoNet_OutputFcn, ...
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


% --- Executes just before GeoNet is made visible.
function GeoNet_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   unrecognized PropertyName/PropertyValue pairs from the
%            command line (see VARARGIN)
clc
clearvars -global geonetGUI
global geonetGUI

% Choose default command line output for GeoNet
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% Create the object associated to the figure in order to manage the whole
% software
geonetGUI = geonetClass(handles);


% UIWAIT makes GeoNet wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = GeoNet_OutputFcn(hObject, eventdata, handles)
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes on button press in bAdjust.
function bAdjust_Callback(hObject, eventdata, handles)
% hObject    handle to bAdjust (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
AdjustmentOpt



function tVal1_Callback(hObject, eventdata, handles)
% hObject    handle to tVal1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tVal1 as text
%        str2double(get(hObject,'String')) returns contents of tVal1 as a double


% --- Executes during object creation, after setting all properties.
function tVal1_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tVal1 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function tVal2_Callback(hObject, eventdata, handles)
% hObject    handle to tVal2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tVal2 as text
%        str2double(get(hObject,'String')) returns contents of tVal2 as a double


% --- Executes during object creation, after setting all properties.
function tVal2_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tVal2 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function tVal3_Callback(hObject, eventdata, handles)
% hObject    handle to tVal3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tVal3 as text
%        str2double(get(hObject,'String')) returns contents of tVal3 as a double


% --- Executes during object creation, after setting all properties.
function tVal3_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tVal3 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function tCov11_Callback(hObject, eventdata, handles)
% hObject    handle to tCov11 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tCov11 as text
%        str2double(get(hObject,'String')) returns contents of tCov11 as a double


% --- Executes during object creation, after setting all properties.
function tCov11_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tCov11 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function tCov12_Callback(hObject, eventdata, handles)
% hObject    handle to tCov12 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tCov12 as text
%        str2double(get(hObject,'String')) returns contents of tCov12 as a double


% --- Executes during object creation, after setting all properties.
function tCov12_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tCov12 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function tCov13_Callback(hObject, eventdata, handles)
% hObject    handle to tCov13 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tCov13 as text
%        str2double(get(hObject,'String')) returns contents of tCov13 as a double


% --- Executes during object creation, after setting all properties.
function tCov13_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tCov13 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function tCov21_Callback(hObject, eventdata, handles)
% hObject    handle to tCov21 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tCov21 as text
%        str2double(get(hObject,'String')) returns contents of tCov21 as a double


% --- Executes during object creation, after setting all properties.
function tCov21_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tCov21 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function tCov22_Callback(hObject, eventdata, handles)
% hObject    handle to tCov22 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tCov22 as text
%        str2double(get(hObject,'String')) returns contents of tCov22 as a double


% --- Executes during object creation, after setting all properties.
function tCov22_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tCov22 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function tCov23_Callback(hObject, eventdata, handles)
% hObject    handle to tCov23 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tCov23 as text
%        str2double(get(hObject,'String')) returns contents of tCov23 as a double


% --- Executes during object creation, after setting all properties.
function tCov23_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tCov23 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function tCov31_Callback(hObject, eventdata, handles)
% hObject    handle to tCov31 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tCov31 as text
%        str2double(get(hObject,'String')) returns contents of tCov31 as a double


% --- Executes during object creation, after setting all properties.
function tCov31_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tCov31 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function tCov32_Callback(hObject, eventdata, handles)
% hObject    handle to tCov32 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tCov32 as text
%        str2double(get(hObject,'String')) returns contents of tCov32 as a double


% --- Executes during object creation, after setting all properties.
function tCov32_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tCov32 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function tCov33_Callback(hObject, eventdata, handles)
% hObject    handle to tCov33 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of tCov33 as text
%        str2double(get(hObject,'String')) returns contents of tCov33 as a double


% --- Executes during object creation, after setting all properties.
function tCov33_CreateFcn(hObject, eventdata, handles)
% hObject    handle to tCov33 (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --------------------------------------------------------------------
function mImport_Callback(hObject, eventdata, handles)
% hObject    handle to mImport (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --------------------------------------------------------------------
function mExport_Callback(hObject, eventdata, handles)
% hObject    handle to mExport (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --------------------------------------------------------------------
function mTSObs_Callback(hObject, eventdata, handles)
% hObject    handle to mTSObs (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.ObsType = 1;
ImportObs

% --------------------------------------------------------------------
function mLev_Callback(hObject, eventdata, handles)
% hObject    handle to mLev (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.ObsType = 2;
ImportObs

% --------------------------------------------------------------------
function mGPS_Callback(hObject, eventdata, handles)
% hObject    handle to mGPS (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.ObsType = 3;
ImportObs

% --------------------------------------------------------------------
function mBase_Callback(hObject, eventdata, handles)
% hObject    handle to mBase (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.ObsType = 4;
ImportObs;

% --------------------------------------------------------------------
function mCoord_Callback(hObject, eventdata, handles)
% hObject    handle to mCoord (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.CoordImpType = 1;
ImportCoord;

% --- Executes when selected object is changed in pVisualization.
function pVisualization_SelectionChangeFcn(hObject, eventdata, handles)
% hObject    handle to the selected object in pVisualization 
% eventdata  structure with the following fields (see UIBUTTONGROUP)
%	EventName: string 'SelectionChanged' (read only)
%	OldValue: handle of the previously selected object or empty if none was selected
%	NewValue: handle of the currently selected object
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.setVisualiPanel

% --- Executes when selected object is changed in pCoord.
function pCoord_SelectionChangeFcn(hObject, eventdata, handles)
% hObject    handle to the selected object in pCoord 
% eventdata  structure with the following fields (see UIBUTTONGROUP)
%	EventName: string 'SelectionChanged' (read only)
%	OldValue: handle of the previously selected object or empty if none was selected
%	NewValue: handle of the currently selected object
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.getHomePropr;
geonetGUI.setCoordPanel;

% --- Executes when selected object is changed in pObsRes.
function pObsRes_SelectionChangeFcn(hObject, eventdata, handles)
% hObject    handle to the selected object in pObsRes 
% eventdata  structure with the following fields (see UIBUTTONGROUP)
%	EventName: string 'SelectionChanged' (read only)
%	OldValue: handle of the previously selected object or empty if none was selected
%	NewValue: handle of the currently selected object
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.getHomePropr;
geonetGUI.setObsResPanel;


% --------------------------------------------------------------------
function mFile_Callback(hObject, eventdata, handles)
% hObject    handle to mFile (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --------------------------------------------------------------------
function mNew_Callback(hObject, eventdata, handles)
% hObject    handle to mNew (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.NewJob


% --------------------------------------------------------------------
function mOpen_Callback(hObject, eventdata, handles)
% hObject    handle to mOpen (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.OpenData;


% --------------------------------------------------------------------
function mSave_Callback(hObject, eventdata, handles)
% hObject    handle to mSave (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.SaveData;

% --------------------------------------------------------------------
function mSaveAs_Callback(hObject, eventdata, handles)
% hObject    handle to mSaveAs (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.SaveDataAs;


% --- Executes when user attempts to close mGeonet.
function mGeonet_CloseRequestFcn(hObject, eventdata, handles)
% hObject    handle to mGeonet (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hint: delete(hObject) closes the figure
global geonetGUI
geonetClass.closeGeoNet(geonetGUI,hObject);


% --- Executes when selected cell(s) is changed in tData.
function tData_CellSelectionCallback(hObject, eventdata, handles)
% hObject    handle to tData (see GCBO)
% eventdata  structure with the following fields (see UITABLE)
%	Indices: row and column indices of the cell(s) currently selecteds
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.tDataCellSelection(eventdata);


% --- Executes on button press in bApprox.
function bApprox_Callback(hObject, eventdata, handles)
% hObject    handle to bApprox (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
switch geonetGUI.AdjType
    case 1
        geonetGUI.removeAdjustment
        geonetGUI.coord = cell(0,18);
        geonetGUI.NameTranslation
        geonetGUI.ApproxCoordComputation
    otherwise
        msgbox('It is not possible to compute approximated coordinates with this configuration','error','Error');
end

% % --- Executes on selection change in popAdjType.
% function popAdjType_Callback(hObject, eventdata, handles)
% % hObject    handle to popAdjType (see GCBO)
% % eventdata  reserved - to be defined in a future version of MATLAB
% % handles    structure with handles and user data (see GUIDATA)
% global geonetGUI
% geonetGUI.setAdjType
% geonetGUI.removeAdjustment
% geonetGUI.EmptyHome
% % Hints: contents = cellstr(get(hObject,'String')) returns popAdjType contents as cell array
% %        contents{get(hObject,'Value')} returns selected item from popAdjType


% --- Executes during object creation, after setting all properties.
function popAdjType_CreateFcn(hObject, eventdata, handles)
% hObject    handle to popAdjType (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --------------------------------------------------------------------
function mSetting_Callback(hObject, eventdata, handles)
% hObject    handle to mSetting (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --------------------------------------------------------------------
function mNetwork_Callback(hObject, eventdata, handles)
% hObject    handle to mNetwork (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --------------------------------------------------------------------
function mConstraints_Callback(hObject, eventdata, handles)
% hObject    handle to mConstraints (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
Constraints

% --------------------------------------------------------------------
function mRFOpt_Callback(hObject, eventdata, handles)
% hObject    handle to mRFOpt (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.SettingType = 1;
SettingRFOpt



% --- Executes when entered data in editable cell(s) in tData.
function tData_CellEditCallback(hObject, eventdata, handles)
% hObject    handle to tData (see GCBO)
% eventdata  structure with the following fields (see UITABLE)
%	Indices: row and column indices of the cell(s) edited
%	PreviousData: previous data for the cell(s) edited
%	EditData: string(s) entered by the user
%	NewData: EditData or its converted form set on the Data property. Empty if Data was not changed
%	Error: error string when failed to convert EditData to appropriate value for Data
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
geonetGUI.tDataCellEdit(eventdata)


% --- Executes on button press in bGraphic.
function bGraphic_Callback(hObject, eventdata, handles)
% hObject    handle to bGraphic (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
if geonetGUI.adjusted == 1
    geonetGUI.viewGraphics(100);
end


% --------------------------------------------------------------------
function mXLS_Callback(hObject, eventdata, handles)
% hObject    handle to mXLS (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
global geonetGUI
if geonetGUI.adjusted == 1
    geonetGUI.coord2xls;
    msgbox('Adjusted coordinates exported correctly!');
else msgbox('Perform adjustment before exporting coordinates','Error');
end
