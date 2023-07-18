%----------------------------------------------------------------------------------------------
%                           GeoNet v0.1.3 beta
%
% Copyright (C) 2014 Lorenzo Rossi, Daniele Sampietro
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

classdef geonetClass < handle      % class definition
    % handles is the father class
    
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % PROPERTIES
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    % constant properties
    properties (Constant)
    end
    % public variables
    properties(GetAccess = 'public', SetAccess = 'public')
        ObsType;            % Observation type, used during data importation
        CoordImpType;       % Coordinate type (approximated or constraints)
        SettingType;        % used to manage settings
    end
    properties(GetAccess = 'public', SetAccess = 'public')
        hh = [];            % home handles
        ih = [];            % observations import figure handles
        oh = [];            % other figures handles
        ch = [];            % constraints handles
        gh = [];            % grafical handle
        ellipsoid;          % saved ellipsoid
        SelectedEllips      % Ellipsoid used during computation
        filename;           % project file name (contain also the full path)
        % Calculation properties
        adjusted = 0;       % Flag to understand if adjustment is performed
        MaxIt = 10;         % Maximum number of iteration in LS adjustment
        geoid;              % Structure where the geoid grid are saved
        SelectedGeoid;      % Used geoid
        LSreport;           % variable to store results of global test
        LSoptions;          % Option for least squares
        AdjType = 1;        % set the kind of adjustment chosed
        constraints = cell(0,9); % cell array to store constraints
        % |1.Use|2.Point|3.X|4.Y|5.Z|6.fi(deg)|7.la(deg)|8.h(deg)|9.point code|
        TS_obs = cell(0,35);% cell array to store Total Station observations
        % |1.Station|2.Point|3.hs(m)|4.Use A|5.Azimuth(gon)|6.Use Z|7.Zenith(gon)|8.Use D|
        % |9.Distance(m)|10.hp|11.Left circ|12.No Prism|13.Pt code|14.St number|15.Sigma A (sec)|
        % |16.Sigma Z(sec)|17.Sigma D(m)|18.SigmaEst A(sec)|19.SigmaEst Z(sec)|20.SigmaEst D (m)|
        % |21.loc test A|22.loc test Z|23.loc test D|24.loc red A|25.loc red Z|26.loc red D|
        % |27.int rel A|28.int rel Z|29.int rel D|30.v A|31.v Z|32.v D|
        % |33.v norm A|34.v norm Z|35.v norm D|
        TS_list = cell(0,6);% list of the stations observed
        % |1.Station|2.hs (m)|3.Sigma hs (m)|4.Beta(rad)|5. sigma beta 
        % estimated (sec)|6.apriori variance for the station (cell)|7.sigma hs estimated (m)
        bl_obs = cell(0,31);% cell array to store baseline observations
        % |1.Use|2.Station|3.Point|4.DX(m)|5.DY(m)|6.DZ(m)|7.Pt code|8.Cxx|
        % |9.Cxy|10.Cxz|11.Cyy|12.Cyz|13.Czz|14.SigmaEst X (m)|15.SigmaEst Y (m)|16.SigmaEst Z (m)|
        % |17.loc test DX|18.loc test DY|19.loc test DZ|20.loc red DX|21.loc red DY|22.loc red DZ|
        % |23.int rel DX|24.int rel DY|25.int rel DZ|26.v DX|27.v DY|28.v DZ|
        % |29.v norm DX|30.v norm DY|31.v norm DZ|
        sp_obs = cell(0,30);% cell array to store single point observations
        % |1.Use|2.Point|3.X(m)|4.Y(m)|5.Z(m)|6.Pt code|7.Cxx|8.Cxy|9.Cxz|
        % |10.Cyy|11.Cyz|12.Czz|13.SigmaEst X (m)|14.SigmaEst Y (m)|15.SigmaEst Z(m)|
        % |16.loc test X|17.loc test Y|18.loc test Z|19.loc red X|20.loc red Y|21.loc red Z|
        % |22.int rel X|23.int rel Y|24.int rel Z|25.v X|26.v Y|27.v Z|
        % |28.v norm X|29.v norm Y|30.v norm Z|
        lev_obs = cell(0,11);% cell array to store levelling observations
        % |1.Use|2.Back point|3.Foreward Point|4.DH (m)|5.Sigma DH (m)|
        % |6.SigmaEst DH(m)|7.loc test DH|8.loc red DH|9.int rel DH|
        % |10.v DH|11.v norm DH|
        coord = cell(0,24);% cell array to store coordinates
        % |1.Point|2.X(m)|3.Y(m)|4.Z(m)|5.Phi(deg)|6.La(deg)|7.h(m)|8.Pt Code|
        % |9.SigmaPhi (m)|10.SigmaLa (m)|11.Sigmah (m)|12.Cxx|13.Cxy|14.Cxz|
        % |15.Cyy|16.Cyz|17.Czz|18.Used in last adjustment (by default before first adj all value are 1)|
        % |19.ext rel fi|20.ext rel la|21.ext rel h|22.ext rel X|23.ext rel Y|24.ext rel Z|
    end
    % private variables (remember to change public to private when work is finished)
    properties( GetAccess = 'private', SetAccess = 'private')
        % Variable used to manage data during calculation ----------------
        D_obs = ones(0,7);        % Observed distances vector
        %|St.|Pt.|Distance|hs|hp|Sigma|St.numb|
        Z_obs = ones(0,8);        % Observed zenith angle vector
        %|St.|Pt.|Zenit|hs|hp|Sigma|Left circ|St.numb|
        A_obs = ones(0,8);        % Observed azimut angle vector
        %|St.|Pt.|Azimut|hs|hp|Sigma|Left circ|St.numb|
        Dh_obs = ones(0,4);       % Observed height differences vector
        %|Back.|For.|Heigth diff.|Sigma|
        GPS_obs = ones(0,10);     % GPS single point observations
        %|Pt.|X|Y|Z|Cxx|Cxy|Cxz|Cyy|Cyz|Czz|
        base_obs = ones(0,11);    % GPS baseline observations
        %|St.|Pt.|X|Y|Z|Cxx|Cxy|Cxz|Cyy|Cyz|Czz|
        St_list_obs = ones(0,4);  % List of TS Stations
        % St.|hs|sigma hs|Beta|
        c_app = [];
        % |Pt.|fi|la|h|
        constXYZ = ones(0,4);
        % |Pt.|X|Y|Z|
        constflh = ones(0,4);
        % |Pt.|fi|la|h|
        
        % Job properties -------------------------------------------------
        Saved = 1;          % Flag to understand if some modifies were done from last saving (1 no modifies, 0 it is modified)
        
        % Data managment properties --------------------------------------
        n_field;            % Number of field to import text file
        fileImp;            % File name of the file to import (observations or coordinates)
        txt_order;          % Order of the field of teh txt file
        txt_del;            % Delimiter when txt file is imported
        txt_header;         % Number of header rows when a txt file is imported
        txt_field;          % list of the name of the field
        txt_del_list;       % list of the possible delimiter
        typeList;           % list of file extension used in selecting file
        
        % GUI managment properties ---------------------------------------
        GUI_typeVis = 0;    % type of data to visualize 0. no data, 1. Observations, 2. Results, 3. Coordinates
        GUI_typeObsRes = 0; % type of observation/result to visualize 0. no visulizization, 1. TS, 2. Lev, 3. GPS sp, 4. GPS bl
        GUI_coord = 0;      % type of coordinate to visualize 0. no visualization, 1. Geodetic, 2. Cartesian
        GUI_lastPath;       % last path used to save or to load a file
        prevHstatus;        % save home previous state when opening new figure
        prevIstatus;        % save import previous state when opening new figure
        prevCstatus;        % save constraints previous state when opening new figure
    end
    
    
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % METHODS
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    % Home GUI functions %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    methods
        % function to create the object
        function obj = geonetClass(handles)
            obj.hh = handles;      % save GUI handles
            % set ellipsoids parameters (name,a,f)
            obj.ellipsoid.GRS80.a = 6378137;
            obj.ellipsoid.GRS80.f = 1 / 298.257222101;
            obj.ellipsoid.WGS84.a = 6378137;
            obj.ellipsoid.WGS84.f = 1 / 298.257223563;
            obj.SelectedEllips = 'GRS80'; % set current ellipsoid
            obj.GUI_lastPath = strcat(pwd,filesep); % identify the current folder
            obj.EmptyHome;
            obj.txt_del_list = {'\t';';';',';' '}; % set the list of field delimiter
            % set all the radio button without selection
            % Visualization group
            set(obj.hh.rObs,'Value',0,'Enable','on')
            set(obj.hh.rRes,'Value',0,'Enable','on')
            set(obj.hh.rCoord,'Value',0,'Enable','on')
            set(obj.hh.bGraphic,'Enable','off');
            % import geoid
            obj.geoid = load('geoid.mat');
            obj.SelectedGeoid = 'EGM08IT';
            % add the path of the software folder to matlab folder
            addpath(obj.GUI_lastPath);
            % set(obj.hh.popAdjType,'Enable','off');
            obj.setDefaultLSoptions;
        end
        
        % set default LS options
        function setDefaultLSoptions(obj)
            % set default LS option
            obj.LSoptions.alfa = 0.05;
            obj.LSoptions.beta = 0.95;
            obj.LSoptions.imax = 20;
            obj.LSoptions.GPSw = 100;
            obj.LSoptions.atmo = false;
            obj.LSoptions.bessel = false;
        end
        
        % function to get home visualization properties
        function getHomePropr(obj) % esclude the case where no button is selected (and so do nothing if it happens)
            % check which radio button is selected in visualization panel
            % and set consequnetly the variable GUI_typeVis, then check for
            % subpanels
            switch get(obj.hh.pVisualization,'SelectedObject') % understand wich object is selected comparing it with the handle of all the possible selection
                % Observations radio button
                case obj.hh.rObs
                    obj.GUI_typeVis = 1;   % set variable of type of visualization to 1 (observations)
                    switch get(obj.hh.pObsRes,'SelectedObject') % get the handle of selected radiobutton and compare it with all the ones present in the panel
                        case obj.hh.rTS % Total station
                            obj.GUI_typeObsRes = 1;
                        case obj.hh.rLev % Levelling
                            obj.GUI_typeObsRes = 2;
                        case obj.hh.rGPS % GPS single point
                            obj.GUI_typeObsRes = 3;
                        case obj.hh.rBase % GPS baseline
                            obj.GUI_typeObsRes = 4;
                    end % end of switch for selected radio button in observations
                    
                    % Results radio button
                case obj.hh.rRes
                    obj.GUI_typeVis = 2; % set variable of type of visualization to 2 (results)
                    switch get(obj.hh.pObsRes,'SelectedObject') % get the handle of selected radiobutton and compare it with all the ones present in the panel
                        case obj.hh.rTS % Total station
                            obj.GUI_typeObsRes = 1;
                        case obj.hh.rLev % levelling
                            obj.GUI_typeObsRes = 2;
                        case obj.hh.rGPS % GPS single point
                            obj.GUI_typeObsRes = 3;
                        case obj.hh.rBase % Baseline
                            obj.GUI_typeObsRes = 4;
                    end % end of switch for selected radio button in results
                    
                    % Coordinates radio button
                case obj.hh.rCoord
                    obj.GUI_typeVis = 3;
                    switch get(obj.hh.pCoord,'SelectedObject') % get the handle of selected radiobutton and compare it with all the ones present in the panel
                        case obj.hh.rGeodetic % geodetic
                            obj.GUI_coord = 1;
                        case obj.hh.rCartesian % cartesian
                            obj.GUI_coord = 2;
                    end % end of switch for selected radio button in coordinates
            end % end of switch to understand visualization selection
        end % end of function
        
        % function to manage the visualization panel
        function setVisualiPanel(obj)
            if ~isempty(get(obj.hh.pVisualization,'SelectedObject'))
                switch get(obj.hh.pVisualization,'SelectedObject') % understand wich object is selected comparing it with the handle of all the possible selection
                    case obj.hh.rObs % Observations radio button
                        obj.GUI_typeVis = 1;
                        obj.setCoordInactive   % set the coordinates panel inactive
                        obj.setObsResActive    % set the observations-results panel active
                        obj.setObsResPanel     % set the visualization on data on the defined panel (using the GUI variable that define the state)
                    case obj.hh.rRes % Results radio button
                        obj.GUI_typeVis = 2;
                        if obj.adjusted == 1 % if calculation is done visulize results
                            obj.setCoordInactive   % set the coordinates panel inactive
                            obj.setObsResActive    % set the observations-results panel active
                            obj.setObsResPanel     % set the visualization on data on the defined panel (using the GUI variable that define the state)
                        elseif obj.adjusted == 0 % if calculation isn't done lock the panel
                            obj.setCoordInactive   % set the coordinates panel inactive
                            obj.setObsResInactive  % set the results panel inactive
                            obj.EmptyHome          % set all the home panel empty
                            set(obj.hh.pVisualization,'SelectedObject',obj.hh.rRes); % set the result button active
                        end
                    case obj.hh.rCoord % Coordinates radio button
                        obj.GUI_typeVis = 3;
                        obj.setCoordActive     % set the coordinates panel active
                        obj.setObsResInactive  % set the observations-results panel inactive
                end
            end
        end
        
        % function to manage observation/results panels
        function setObsResPanel(obj)
            switch obj.GUI_typeVis % evaluate if we are treating results or observations
                case 1 % Observations
                    obj.setObsPanel;
                case 2 % Results
                    if obj.adjusted == 0
                        obj.EmptyHome;
                        set(obj.hh.pVisualization,'SelectedObject',obj.hh.rRes); % set the result button active
                    else
                        obj.setResPanel;
                    end
            end % end of switch
        end
        
        % function to manage obs/res panel when obs are visualized
        function setObsPanel(obj)
            switch obj.GUI_typeObsRes % check wich type of observation must be visualized
                case 0 % no selection before
                    obj.EmptyHome;       % set the home as empty figure
                    % Observation-results group: set all the button active. No default
                    % selection (they maintain the last one)
                    set(obj.hh.rTS,'Value',0,'Enable','on');
                    set(obj.hh.rLev,'Value',0,'Enable','on');
                    set(obj.hh.rGPS,'Value',0,'Enable','on');
                    set(obj.hh.rBase,'Value',0,'Enable','on');
                case 1 % Total station
                    % Activate the right panel for instrumental error
                    set(obj.hh.pCovMat,'Visible','off')    % Covariance Matrix panel
                    set(obj.hh.pInstErr,'Visible','on')    % Instrumental error panel
                    set(obj.hh.pEmpty,'Visible','off')     % Activating empty panel
                    % set the panel of instrumental error
                    set(obj.hh.tVal1,'Visible','on','String','')              % Azimuth error
                    set(obj.hh.tVal2,'Visible','on','String','')              % Zenith error
                    set(obj.hh.tVal3,'Visible','on','String','')              % Distance error
                    set(obj.hh.tDesc1,'Visible','on','String','Azimut:')      % Azimuth description
                    set(obj.hh.tDesc2,'Visible','on','String','Zenith:')      % Zenith description
                    set(obj.hh.tDesc3,'Visible','on','String','Distance:')    % Distance description
                    drawnow
                    pause(0.001)
                    % define table header and its parameter (width, format, editable)
                    c_desc = {'Station|Name','Point|Name','St Inst|h (m)','Use','Azimuth|(gon)', ...
                        'Use','Zenith|(gon)','Use','Distance|(m)','Target|h (m)',...
                        'Left C.|Obs.','No|prims','Point Code|or description'};                % Column description
                    c_width = {105,105,45,27,80,27,80,27,80,50,40,40,140};       % Column width
                    c_format = {'char','char','char','logical','numeric','logical','char','logical',...
                        'char','char','logical','logical','char'};      % Column format
                    c_editable = [false false false true false true ...
                        false true false false true true false];                          % Editable column
                    % set parameter in table
                    set(obj.hh.tData,'ColumnName',c_desc,...
                        'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable,'RowName', {'numbered'} );
                    if isempty(obj.TS_obs) == 0 % check if there are observations of the requested type
                        Data = obj.TS_obs(:,[1 2 3 4 5 6 7 8 9 10 11 12 13]);
                        Data(:,[3 9 10]) = cellfun(@(x) sprintf('%.3f',x), Data(:,[3 9 10]),'UniformOutput',false);
                        Data(:,[5 7]) = cellfun(@(x) sprintf('%.4f',x), Data(:,[5 7]),'UniformOutput',false);
                        set(obj.hh.tData,'Data',Data);
                    else set(obj.hh.tData,'Data',[]);
                    end
                    
                case 2 % Levelling
                    % Activate the right panel for instrumental error
                    set(obj.hh.pCovMat,'Visible','off')    % Covariance Matrix panel
                    set(obj.hh.pInstErr,'Visible','on')    % Instrumental error panel
                    set(obj.hh.pEmpty,'Visible','off')     % Activating empty panel
                    % set the panel of instrumental error
                    set(obj.hh.tVal1,'Visible','off')                         % Not used error
                    set(obj.hh.tVal2,'Visible','on','String','')              % Delta heigth error
                    set(obj.hh.tVal3,'Visible','off')                         % Not used error
                    set(obj.hh.tDesc1,'Visible','off')                        % Not used description
                    set(obj.hh.tDesc2,'Visible','on','String','Delta height:')% Delta heigth description
                    set(obj.hh.tDesc3,'Visible','off')                        % Not used description
                    drawnow
                    pause(0.001)
                    c_desc = {'Use','Backsight|point','Foresight|point','Height|Difference'};   % column name
                    c_width = {30, 100, 100, 60};                                               % column width
                    c_format = {'logical', 'char', 'char', 'char'};                            % column format
                    c_editable = [true, false, false, false];                                   % column editable
                    % set parameter in table
                    set(obj.hh.tData,'ColumnName',c_desc,...
                        'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable, 'RowName', {'numbered'});
                    if isempty(obj.lev_obs) == 0 % check f there are observations of the requested type
                        Data = obj.lev_obs(:,[1 2 3 4]);
                        Data(:,4) = cellfun(@(x) sprintf('%.4f',x), Data(:,4),'UniformOutput',false);
                        set(obj.hh.tData,'Data',Data);
                    else set(obj.hh.tData,'Data',[]);
                    end
                    
                case 3 % GPS single point
                    % Activate the right panel for instrumental error
                    set(obj.hh.pCovMat,'Visible','on')     % Covariance Matrix panel
                    set(obj.hh.pInstErr,'Visible','off')   % Instrumental error panel
                    set(obj.hh.pEmpty,'Visible','off')     % Activating empty panel
                    % set the value of covariance matrix to empty string
                    obj.EmptyCovPanel('Instrumental covariance matrix')
                    drawnow
                    pause(0.001)
                    c_desc = {'Use','Point','X (m)','Y (m)', 'Z (m)', 'Point code',};           % column name
                    c_width = {30, 100, 100, 100, 100, 150};                                    % column width
                    c_format = {'logical', 'char', 'char', 'char', 'char', 'char' };         % column format
                    c_editable = [true, false, false, false, false, false];                     % column editable
                    % set parameter in table
                    set(obj.hh.tData,'ColumnName',c_desc,...
                        'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable, 'RowName', {'numbered'});
                    if isempty(obj.sp_obs) == 0 % check f there are observations of the requested type
                        Data = obj.sp_obs(:,[1 2 3 4 5 6]);
						Data(:,3:5) = cellfun(@(x) sprintf('%.3f',x), Data(:,3:5), 'UniformOutput',false);
                        set(obj.hh.tData,'Data',Data);
                    else set(obj.hh.tData,'Data',[]);
                    end
                    
                case 4 % GPS baseline
                    % Activate the right panel for instrumental error
                    set(obj.hh.pCovMat,'Visible','on')     % Covariance Matrix panel
                    set(obj.hh.pInstErr,'Visible','off')   % Instrumental error panel
                    set(obj.hh.pEmpty,'Visible','off')     % Activating empty panel
                    % set the value of covariance matrix to empty string
                    obj.EmptyCovPanel('Instrumental covariance matrix')
                    drawnow
                    pause(0.001)
                    c_desc = {'Use','Station','Point','X Diff. (m)','Y Diff. (m)', 'Z Diff. (m)', 'Point code',}; % column name
                    c_width = {30, 100, 100, 100, 100, 100, 150};                               % column width
                    c_format = {'logical', 'char', 'char', 'short', 'short', 'short', 'char' };         % column format
                    c_editable = [true, false, false, false, false, false, false];              % column editable
                    % set parameter in table
                    set(obj.hh.tData,'ColumnName',c_desc,...
                        'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable, 'RowName', {'numbered'});
                    if isempty(obj.bl_obs) == 0 % check if there are observations of the requested type
					    Data = obj.bl_obs(:,[1 2 3 4 5 6 7]);
						Data(:,4:6) = cellfun(@(x) sprintf('%.3f',x), Data(:,4:6), 'UniformOutput',false);
                        set(obj.hh.tData,'Data',Data);
                    else set(obj.hh.tData,'Data',[]);
                    end
            end
        end
        
        % function to manage obs/res panel when res are visualized
        function setResPanel(obj)
            switch obj.GUI_typeObsRes % check wich type of results must be visualized
                case 0 % no selection before
                    obj.EmptyHome;       % set the home as empty figure
                    % Observation-results group: set all the button active. No default
                    % selection (they maintain the last one)
                    set(obj.hh.rTS,'Value',0,'Enable','on');
                    set(obj.hh.rLev,'Value',0,'Enable','on');
                    set(obj.hh.rGPS,'Value',0,'Enable','on');
                    set(obj.hh.rBase,'Value',0,'Enable','on');
                case 1 % Total station
                    % Activate the right panel for instrumental error
                    set(obj.hh.pCovMat,'Visible','off')    % Covariance Matrix panel
                    set(obj.hh.pInstErr,'Visible','on')    % Instrumental error panel
                    set(obj.hh.pEmpty,'Visible','off')     % Activating empty panel
                    % set the panel of instrumental error
                    set(obj.hh.tVal1,'Visible','on','String','')              % Azimuth error
                    set(obj.hh.tVal2,'Visible','on','String','')              % Zenith error
                    set(obj.hh.tVal3,'Visible','on','String','')              % Distance error
                    set(obj.hh.tDesc1,'Visible','on','String','Azimut:')      % Azimuth description
                    set(obj.hh.tDesc2,'Visible','on','String','Zenith:')      % Zenith description
                    set(obj.hh.tDesc3,'Visible','on','String','Distance:')    % Distance description
                    drawnow
                    pause(0.001)
                    % define table header and its parameter (width, format, editable)
                    c_desc = {'Station|Name','Point|Name','Used|A', 'Used|Z', 'Used|D', ...
                        'std A|(sec)', 'std Z|(sec)', 'std D|(mm)',...
                        'test|A', 'test|Z', 'test|D',...
                        'loc|red A', 'loc|red Z', 'loc|red D',...
                        'Int|rel A', 'Int|rel Z', 'Int|rel D',...
                        'Right|Circle','No|prism','Point Code'};
                    c_width = {105,105,30,30,30,...
                        40,40,40,...
                        30,30,30,...
                        40,40,40,...
                        40,40,40,...
                        40,40,140};       % Column width
                    c_format = {'char','char','logical','logical','logical',...
                        'char','char', 'char',...
                        'logical','logical','logical',...
                        'char','char', 'char',...
                        'char','char', 'char',...
                        'logical','logical','char'};      % Column format
                    c_editable = false(1,20);             % Editable column
                    % set parameter in table
                    set(obj.hh.tData,'ColumnName',c_desc,...
                        'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable, 'RowName', {'numbered'});
                    if isempty(obj.TS_obs) == 0 % check if there are observations of the requested type
                        Data = obj.TS_obs(:,[1,2,4,6,8,...
                            18,19,20,...
                            21,22,23,...
                            24,25,26,...
                            27,28,29,...
                            11,12,13]);
                        Data(:,[6 7]) = cellfun(@(x) sprintf('   %.1f"',x), Data(:,[6 7]),'UniformOutput',false); % std angles
                        Data(:,[15 16]) = cellfun(@(x) sprintf('   %.1f"',x .* 180 ./ pi .* 3600), Data(:,[15 16]),'UniformOutput',false); % internal reliab angles
                        Data(:,[12 13 14]) = cellfun(@(x) sprintf('   %.2f',x), Data(:,[12 13 14]),'UniformOutput',false); % local redoundacy
                        Data(:,8) = cellfun(@(x) sprintf('   %.2f',x.*1000), Data(:,8),'UniformOutput',false); % std distance
                        Data(:,17) = cellfun(@(x) sprintf('   %.2f',x.*1000), Data(:,17),'UniformOutput',false); % internal reliab distances
                        set(obj.hh.tData,'Data',Data);
                    else set(obj.hh.tData,'Data',[]);
                    end 
                    
                case 2 % Levelling
                    % Activate the right panel for instrumental error
                    set(obj.hh.pCovMat,'Visible','off')    % Covariance Matrix panel
                    set(obj.hh.pInstErr,'Visible','on')    % Instrumental error panel
                    set(obj.hh.pEmpty,'Visible','off')     % Activating empty panel
                    % set the panel of instrumental error
                    set(obj.hh.tVal1,'Visible','off')                         % Not used error
                    set(obj.hh.tVal2,'Visible','on','String','')              % Delta heigth error
                    set(obj.hh.tVal3,'Visible','off')                         % Not used error
                    set(obj.hh.tDesc1,'Visible','off')                        % Not used description
                    set(obj.hh.tDesc2,'Visible','on','String','Delta height:')% Delta heigth description
                    set(obj.hh.tDesc3,'Visible','off')                        % Not used description
                    drawnow
                    pause(0.001)
                    c_desc = {'Used','Backsight|point','Foresight|point',...
                        'std DH|(mm)','loc|test DH','loc|red DH','Int|rel DH'};   % column name
                    c_width = {30, 100, 100, 50, 50, 50, 50};                                               % column width
                    c_format = {'logical', 'char', 'char', 'char', 'logical',...
                        'char','logical'};                        % column format
                    c_editable = false(1,7);                                   % column editable
                    % set parameter in table
                    set(obj.hh.tData,'ColumnName',c_desc,...
                        'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable, 'RowName', {'numbered'});
                    if isempty(obj.lev_obs) == 0 % check f there are observations of the requested type
                        Data = obj.lev_obs(:,[1 2 6 7 8 9]);
                        Data(:,[4 6]) = cellfun(@(x) sprintf(' %.2f',x), Data(:,[4 6]),'UniformOutput',false);
                        set(obj.hh.tData,'Data',Data);
                    else set(obj.hh.tData,'Data',[]);
                    end
                    
                case 3 % GPS single point
                    % Activate the right panel for instrumental error
                    set(obj.hh.pCovMat,'Visible','on')     % Activating Covariance Matrix panel
                    set(obj.hh.pInstErr,'Visible','off')   % Instrumental error panel
                    set(obj.hh.pEmpty,'Visible','off')     % empty panel
                    obj.EmptyCovPanel('Instrumental covariance matrix');   % set all the value of covariance matrix
                    drawnow
                    pause(0.001)
                    
                    c_desc = {'Used','Point','std X|(mm)','std Y|(mm)','std Z|(mm)',...
                        'loc|test X','loc|test Y','loc|test Z',...
                        'loc|red X','loc|red Y', 'loc|red Z', ...
                        'int|rel X','int|rel Y','int|rel Z','Point code'};                      % column name
                    c_width = {30, 100, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 100};   % column width
                    c_format = {'logical', 'char', 'char', 'char', 'char',...
                        'logical', 'logical', 'logical',...
                        'char', 'char', 'char',...
                        'char', 'char', 'char',...
                        'char' };         % column format
                    c_editable = false(1,24);                     % column editable
                    % set parameter in table
                    set(obj.hh.tData,'ColumnName',c_desc,...
                        'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable, 'RowName', {'numbered'});
                    if isempty(obj.sp_obs) == 0 % check f there are observations of the requested type
                        Data = obj.sp_obs(:,[1 2 13 14 15 16 17 18 19 20 21 22 23 24 8]);
                        Data(:,[3 4 5 12 13 14]) = cellfun(@(x) sprintf('    %.2f',x.*1000), Data(:,[3 4 5 12 13 14]),'UniformOutput',false);
                        Data(:,[9 10 11]) = cellfun(@(x) sprintf('    %.2f',x), Data(:,[9 10 11]),'UniformOutput',false);
                        set(obj.hh.tData,'Data',Data);
                    else set(obj.hh.tData,'Data',[]);
                    end
                    
                case 4 % GPS baseline
                    % Activate the right panel for instrumental error
                    set(obj.hh.pCovMat,'Visible','on')     % Covariance Matrix panel
                    set(obj.hh.pInstErr,'Visible','off')   % Instrumental error panel
                    set(obj.hh.pEmpty,'Visible','off')     % Activating empty panel
                    obj.EmptyCovPanel('Instrumental covariance matrix')
                    drawnow
                    pause(0.001)
                    
                    c_desc = {'Used','Station','Point','std DX|(mm)','std DY|(mm)','std DZ|(mm)',...
                        'loc|test DX','loc|test DY','loc|test DZ',...
                        'loc|red DX','loc|red DY', 'loc|red DZ', ...
                        'int|rel DX','int|rel DY','int|rel DZ','Point code'};                      % column name
                    c_width = {30, 100, 100, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 100};   % column width
                    c_format = {'logical',  'char', 'char', 'char', 'char', 'char',...
                        'logical', 'logical', 'logical',...
                        'char', 'char', 'char',...
                        'char', 'char', 'char',...
                        'char' };         % column format
                    c_editable = false(1,25);                     % column editable
                    % set parameter in table
                    set(obj.hh.tData,'ColumnName',c_desc,...
                        'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable, 'RowName', {'numbered'});
                    if isempty(obj.bl_obs) == 0 % check if there are observations of the requested type
                        Data = obj.bl_obs(:,[1 2 3 14 15 16 17 18 19 20 21 22 23 24 25 7]);
                        Data(:,[4 5 6 13 14 15]) = cellfun(@(x) sprintf('    %.2f',x.*1000), Data(:,[4 5 6 13 14 15]),'UniformOutput',false);
                        Data(:,[10 11 12]) = cellfun(@(x) sprintf('    %.2f',x), Data(:,[10 11 12]),'UniformOutput',false);
                        set(obj.hh.tData,'Data',Data);
                    else set(obj.hh.tData,'Data',[]);
                    end
            end
        end
        
        % function to manage cooridnates panel when res are visualized
        function setCoordPanel(obj)
            switch obj.adjusted % check if the computation is done or not
                case 0 % not adjusted yet
                    switch obj.GUI_coord % check coordinates type
                        case 1 % geodetic
                            % Activate the right panel for instrumental error
                            set(obj.hh.pCovMat,'Visible','off')     % Covariance Matrix panel
                            set(obj.hh.pInstErr,'Visible','off')    % Instrumental error panel
                            set(obj.hh.pEmpty,'Visible','on')       % Activating empty panel
                            set(obj.hh.tData,'Data',[]);
                            drawnow
                            pause(0.001)
                            c_desc = {'Point','Longitude','Latitude', 'Ellip. heigth', 'Point code'};   % column name
                            c_width = {150, 150, 150, 70, 100};                                        % column width
                            c_format = {'char', 'numeric', 'numeric','short', 'char'};                        % column format
                            c_editable = [false, false, false, false, false];                           % column editable
                            % set parameter in table
                            set(obj.hh.tData,'ColumnName',c_desc,...
                                'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable,'RowName', {'numbered'});
                            if isempty(obj.coord) == 0 % check f there are observations of the requested type
                                Data = obj.coord(:,[1 5 6 7 8]);  % extract the column with needed data
                                Data(:,[2 3]) = cellfun(@obj.dec2sex,Data(:,[2 3]),'UniformOutput',0);   % convert angles in sexagesimal view
                                Data(:,4) = cellfun(@(x) sprintf('%.3f',x),Data(:,4),'UniformOutput',0); % convert heigth in 3 decimal position numbers
                                set(obj.hh.tData,'Data',Data);
                            else set(obj.hh.tData,'Data',[]);
                            end
                        case 2 % cartesian
                            set(obj.hh.pCovMat,'Visible','off')     % Covariance Matrix panel
                            set(obj.hh.pInstErr,'Visible','off')    % Instrumental error panel
                            set(obj.hh.pEmpty,'Visible','on')       % Activating empty panel
                            set(obj.hh.tData,'Data',[]);
                            drawnow
                            pause(0.001)
                            c_desc = {'Point','X (m)','Y (m)', 'Z (m)', 'Point code'};                  % column name
                            c_width = {150, 200, 200, 200, 100};                                        % column width
                            c_format = {'char', 'short', 'short','short', 'char'};                      % column format
                            c_editable = [false, false, false, false, false];                           % column editable
                            % set parameter in table
                            set(obj.hh.tData,'ColumnName',c_desc,...
                                'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable,'RowName', {'numbered'});
                            if isempty(obj.coord) == 0 % check f there are observations of the requested type
                                Data = obj.coord(:,[1 2 3 4 8]); % extract the column with the needed data
                                Data(:,2:4) = cellfun(@(x) strcat(sprintf('%.3f',x)),Data(:,2:4),'UniformOutput',false);
                                set(obj.hh.tData,'Data',Data);
                            else set(obj.hh.tData,'Data',[]);
                            end
                    end
                case 1 % adjusted
                    switch obj.GUI_coord % check coordinates type
                        case 1 % geodetic
                            % Activate the right panel for instrumental error
                            set(obj.hh.pCovMat,'Visible','on')      % Activate Covariance Matrix panel
                            set(obj.hh.pInstErr,'Visible','off')    % Instrumental error panel
                            set(obj.hh.pEmpty,'Visible','off')      % Deactivating empty panel
                            obj.EmptyCovPanel('Estimated covariance matrix');
                            set(obj.hh.tData,'Data',[]);
                            drawnow
                            pause(0.001)
                            c_desc = {'Point','Latitude','Longitude',...
                                'Ellip. heigth','std E|(mm)', 'std N|(mm)', 'std h|(mm)',...
                                'Ext rel|E (mm)', 'Ext rel|N (mm)', 'Ext rel|h (mm)','Point code'};         % column name
                            c_width = {100, 100, 100, 100, 60, 60, 60, 60, 60, 60, 100};                                    % column width
                            c_format = {'char', 'char', 'char',...
                                'char', 'char', 'char', 'char',...
                                'char', 'char', 'char', 'char'};       % column format
                            c_editable = [false, false, false,...
                                false, false, false, false,...
                                false, false, false, false];              % column editable
                            % set parameter in table
                            set(obj.hh.tData,'ColumnName',c_desc,...
                                'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable,'RowName', {'numbered'});
                            if isempty(obj.coord) == 0 % check f there are observations of the requested type
                                Data = obj.coord(:,[1 5 6 7 9 10 11 19 20 21 8]);  % extract the column with needed data
                                Data(:,[2 3]) = cellfun(@obj.dec2sex,Data(:,[2 3]),'UniformOutput',0);   % convert angles in sexagesimal view
                                Data(:,4) = cellfun(@(x) sprintf('%.3f',x),Data(:,4),'UniformOutput',0); % convert heigth in 3 decimal position numbers
                                Data(:,[5 6 7]) = cellfun(@(x) sprintf('     %.2f',x),Data(:,[5 6 7]),'UniformOutput',0); % convert sigma in 2 decimal position numbers
                                Data(:,[8 9 10]) = cellfun(@(x) sprintf('     %.2f',x.*1000),Data(:,[8 9 10]),'UniformOutput',0); % external reliability [mm]
                                set(obj.hh.tData,'Data', Data);
                            else set(obj.hh.tData,'Data',[]);
                            end
                        case 2 % cartesian
                            set(obj.hh.pCovMat,'Visible','on')      % Covariance Matrix panel
                            set(obj.hh.pInstErr,'Visible','off')    % Instrumental error panel
                            set(obj.hh.pEmpty,'Visible','off')      % Activating empty panel
                            set(obj.hh.tData,'Data',[]);
                            obj.EmptyCovPanel('Estimated covariance matrix');
                            drawnow
                            pause(0.001)
                            c_desc = {'Point','X (m)', 'Y (m)', 'Z (m)', 'Std X|(mm)', 'Std Y|(mm)', 'Std Z|(mm)',...
                                'Ext rel|X (mm)', 'Ext rel|Y (mm)', 'Ext rel|Z (mm)','Point code'};           % column name
                            c_width = {100, 100, 100, 100, 60, 60, 60, 60, 60, 60, 100};                                        % column width
                            c_format = {'char', 'char', 'char','char','char','char','char', 'char','char','char', 'char'};                 % column format
                            c_editable = false(1,11);                                                                % column editable
                            % set parameter in table
                            set(obj.hh.tData,'ColumnName',c_desc,...
                                'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable,'RowName', {'numbered'});
                            if isempty(obj.coord) == 0 % check f there are observations of the requested type
                                Data = obj.coord(:,[1 2 3 4 12 15 17 22 23 24 8]); % extract the column with the needed data
                                Data(:,[2 3 4]) = cellfun(@(x) strcat(sprintf('%.3f',x)), Data(:,[2 3 4]),'UniformOutput',false);
                                Data(:,[5 6 7]) = cellfun(@(x) sprintf('     %.2f',sqrt(x).*1e3), Data(:,[5 6 7]),'UniformOutput',false);
                                Data(:,[8 9 10]) = cellfun(@(x) sprintf('     %.2f',x.*1000),Data(:,[8 9 10]),'UniformOutput',0); % external reliability [mm]
                                set(obj.hh.tData,'Data',Data);
                            else set(obj.hh.tData,'Data',[]);
                            end
                    end
            end
        end % end setCoordPanel function
        
        % function to set the home in empty state (e.g. during opening
        % function)
        function EmptyHome(obj)
            % set the table with data visualization as empty one
            set(obj.hh.tData,'Data',{});
            set(obj.hh.tData,'RowName',{});
            set(obj.hh.tData,'ColumnName',{});
            
            % deactivate all the panels with variance and covariance of the
            % observations
            set(obj.hh.pCovMat,'Visible','off')     % Covariance Matrix panel
            set(obj.hh.pInstErr,'Visible','off')    % Instrumental error panel
            set(obj.hh.pEmpty,'Visible','on')       % Activating empty panel
            % Visualization group (remove selection from every radio
            % button)
            % set(obj.hh.pVisualization,'SelectedObject',[]);
            % Observation-results group
            obj.setObsResInactive
            % Coordinates group
            obj.setCoordInactive
        end
        
		% set an empty covariance panel
        function EmptyCovPanel(obj,title)
            set(obj.hh.tCov11,'String','');
            set(obj.hh.tCov12,'String','');
            set(obj.hh.tCov13,'String','');
            set(obj.hh.tCov21,'String','');
            set(obj.hh.tCov22,'String','');
            set(obj.hh.tCov23,'String','');
            set(obj.hh.tCov31,'String','');
            set(obj.hh.tCov32,'String','');
            set(obj.hh.tCov33,'String','');
            if exist('title','var')
                if ischar(title)
                    set(obj.hh.pCovMat,'Title',title);
                else set(obj.hh.pCovMat,'Title','Covariance Matrix');
                end
            else set(obj.hh.pCovMat,'Title','Covariance Matrix');
            end
            
        end
        
        % set observations-results radio buttons inactive
        function setObsResInactive(obj)
            % Observation-results group
            set(obj.hh.rTS,'Value',0,'Enable','off')
            set(obj.hh.rLev,'Value',0,'Enable','off')
            set(obj.hh.rGPS,'Value',0,'Enable','off')
            set(obj.hh.rBase,'Value',0,'Enable','off')
        end
        
        % set observations radio buttons active
        function setObsResActive(obj)
            switch obj.AdjType
                case {1, 2}
                    % Observation-results group: set all the button active. No default
                    % selection (they maintain the last one)
                    set(obj.hh.rTS,'Value',0,'Enable','on');
                    set(obj.hh.rLev,'Value',0,'Enable','on');
                    set(obj.hh.rGPS,'Value',0,'Enable','on');
                    set(obj.hh.rBase,'Value',0,'Enable','on');
                    switch obj.GUI_typeObsRes % check wich value was set before and reset it active
                        case 1 % total station
                            set(obj.hh.rTS,'Value',1);
                        case 2 % levelling
                            set(obj.hh.rLev,'Value',1);
                        case 3 % GPS single point
                            set(obj.hh.rGPS,'Value',1);
                        case 4 % Baseline GPS
                            set(obj.hh.rBase,'Value',1);
                    end % end switch
                    obj.setObsResPanel; % set the previous visualization given from the panel
            end
            
        end
        
        % set observations-results radio buttons inactive
        function setCoordInactive(obj)
            % Coordinates group
            set(obj.hh.rGeodetic,'Value',0,'Enable','off')
            set(obj.hh.rCartesian,'Value',0,'Enable','off')
        end
        
        % set coordinates radio buttons active
        function setCoordActive(obj)
            switch obj.AdjType % verify wich kind of adjustment is chosen
                case {1, 2, 5} % global RF network
                    % Coordinates group
                    set(obj.hh.rGeodetic,'Value',0,'Enable','on')
                    set(obj.hh.rCartesian,'Value',0,'Enable','on')
                    switch obj.GUI_coord % check wich value was set before and reset it active
                        case 1 % Geodetic
                            set(obj.hh.rGeodetic,'Value',1)
                            obj.setCoordPanel;
                        case 2 % Geocentric
                            set(obj.hh.rCartesian,'Value',1)
                            obj.setCoordPanel;
                    end % end of switch
                case {3, 4} % local RF network
                    % Coordinates group
                    set(obj.hh.rGeodetic,'Value',0,'Enable','off')
                    set(obj.hh.rCartesian,'Value',1,'Enable','on')
                    obj.GUI_coord = 2; % set cartesian coordinates
                    obj.setCoordPanel;
            end
        end % end of setCoordActive
        
        % function to write instrumental error in the home
        function tDataCellSelection(obj, eventdata)
            if size(eventdata.Indices,1) >= 1
                r = eventdata.Indices(1,1);
                switch obj.GUI_typeVis
                    case {1, 2} % observations, results
                        switch obj.GUI_typeObsRes
                            case 1 % total station
                                set(obj.hh.tVal1,'String',sprintf('%d"',obj.TS_obs{r,15}),'HorizontalAlignment','center');
                                set(obj.hh.tVal2,'String',sprintf('%d"',obj.TS_obs{r,16}),'HorizontalAlignment','center');
                                set(obj.hh.tVal3,'String',sprintf('%.2f mm',obj.TS_obs{r,17}*1e3),'HorizontalAlignment','center');
                            case 2 % levelling
                                set(obj.hh.tVal2,'String',sprintf('%.2f mm',obj.lev_obs{r,5}),'HorizontalAlignment','center');
                            case 3 % GPS
                                set(obj.hh.tCov11,'String',sprintf('%.3e',obj.sp_obs{r,7}),'HorizontalAlignment','center');
                                set(obj.hh.tCov12,'String',sprintf('%.3e',obj.sp_obs{r,8}),'HorizontalAlignment','center');
                                set(obj.hh.tCov13,'String',sprintf('%.3e',obj.sp_obs{r,9}),'HorizontalAlignment','center');
                                set(obj.hh.tCov21,'String',sprintf('%.3e',obj.sp_obs{r,8}),'HorizontalAlignment','center');
                                set(obj.hh.tCov22,'String',sprintf('%.3e',obj.sp_obs{r,10}),'HorizontalAlignment','center');
                                set(obj.hh.tCov23,'String',sprintf('%.3e',obj.sp_obs{r,11}),'HorizontalAlignment','center');
                                set(obj.hh.tCov31,'String',sprintf('%.3e',obj.sp_obs{r,9}),'HorizontalAlignment','center');
                                set(obj.hh.tCov32,'String',sprintf('%.3e',obj.sp_obs{r,11}),'HorizontalAlignment','center');
                                set(obj.hh.tCov33,'String',sprintf('%.3e',obj.sp_obs{r,12}),'HorizontalAlignment','center');
                            case 4 % baselines
                                set(obj.hh.tCov11,'String',sprintf('%.3e',obj.bl_obs{r,8}),'HorizontalAlignment','center');
                                set(obj.hh.tCov12,'String',sprintf('%.3e',obj.bl_obs{r,9}),'HorizontalAlignment','center');
                                set(obj.hh.tCov13,'String',sprintf('%.3e',obj.bl_obs{r,10}),'HorizontalAlignment','center');
                                set(obj.hh.tCov21,'String',sprintf('%.3e',obj.bl_obs{r,9}),'HorizontalAlignment','center');
                                set(obj.hh.tCov22,'String',sprintf('%.3e',obj.bl_obs{r,11}),'HorizontalAlignment','center');
                                set(obj.hh.tCov23,'String',sprintf('%.3e',obj.bl_obs{r,12}),'HorizontalAlignment','center');
                                set(obj.hh.tCov31,'String',sprintf('%.3e',obj.bl_obs{r,10}),'HorizontalAlignment','center');
                                set(obj.hh.tCov32,'String',sprintf('%.3e',obj.bl_obs{r,12}),'HorizontalAlignment','center');
                                set(obj.hh.tCov33,'String',sprintf('%.3e',obj.bl_obs{r,13}),'HorizontalAlignment','center');
                        end
                    case 3 % coordinates
                        if obj.adjusted == 1
                            set(obj.hh.tCov11,'String',sprintf('%.3e',obj.coord{r,12}),'HorizontalAlignment','center');
                            set(obj.hh.tCov12,'String',sprintf('%.3e',obj.coord{r,13}),'HorizontalAlignment','center');
                            set(obj.hh.tCov13,'String',sprintf('%.3e',obj.coord{r,14}),'HorizontalAlignment','center');
                            set(obj.hh.tCov21,'String',sprintf('%.3e',obj.coord{r,13}),'HorizontalAlignment','center');
                            set(obj.hh.tCov22,'String',sprintf('%.3e',obj.coord{r,15}),'HorizontalAlignment','center');
                            set(obj.hh.tCov23,'String',sprintf('%.3e',obj.coord{r,16}),'HorizontalAlignment','center');
                            set(obj.hh.tCov31,'String',sprintf('%.3e',obj.coord{r,14}),'HorizontalAlignment','center');
                            set(obj.hh.tCov32,'String',sprintf('%.3e',obj.coord{r,16}),'HorizontalAlignment','center');
                            set(obj.hh.tCov33,'String',sprintf('%.3e',obj.coord{r,17}),'HorizontalAlignment','center');
                        end
                end
            else
                switch obj.GUI_typeVis
                    case 1 % observations, results
                        switch obj.GUI_typeObsRes
                            case 1 % total station
                                set(obj.hh.tVal1,'String','');
                                set(obj.hh.tVal2,'String','');
                                set(obj.hh.tVal3,'String','');
                            case 2 % levelling
                                set(obj.hh.tVal2,'String','');
                            case 3 % GPS
                                set(obj.hh.tCov11,'String','');
                                set(obj.hh.tCov12,'String','');
                                set(obj.hh.tCov13,'String','');
                                set(obj.hh.tCov21,'String','');
                                set(obj.hh.tCov22,'String','');
                                set(obj.hh.tCov23,'String','');
                                set(obj.hh.tCov31,'String','');
                                set(obj.hh.tCov32,'String','');
                                set(obj.hh.tCov33,'String','');
                            case 4 % baselines
                                set(obj.hh.tCov11,'String','');
                                set(obj.hh.tCov12,'String','');
                                set(obj.hh.tCov13,'String','');
                                set(obj.hh.tCov21,'String','');
                                set(obj.hh.tCov22,'String','');
                                set(obj.hh.tCov23,'String','');
                                set(obj.hh.tCov31,'String','');
                                set(obj.hh.tCov32,'String','');
                                set(obj.hh.tCov33,'String','');
                        end
                    case 2 % coordinates
                        if obj.adjusted == 1
                            set(obj.hh.tCov11,'String','');
                            set(obj.hh.tCov12,'String','');
                            set(obj.hh.tCov13,'String','');
                            set(obj.hh.tCov21,'String','');
                            set(obj.hh.tCov22,'String','');
                            set(obj.hh.tCov23,'String','');
                            set(obj.hh.tCov31,'String','');
                            set(obj.hh.tCov32,'String','');
                            set(obj.hh.tCov33,'String','');
                        end
                end
            end
        end
        
        % function to modify value of data in table
        function tDataCellEdit(obj, eventdata)
            r = eventdata.Indices(1,1);
            c = eventdata.Indices(1,2);
            switch obj.GUI_typeVis % verify the kind of data visualized
                case 1  % observations
                    switch obj.GUI_typeObsRes
                        case 1 % total station
                            obj.TS_obs{r,c} = eventdata.NewData;
                            % case of change between prism or not
                            % (instrumental std need to be changed)
                            if c == 12 && eventdata.NewData == 1
                                nst = obj.TS_obs{r,14};
                                obj.TS_obs{r,17} = obj.TS_list{nst,6}{2,1} *1e-3 + obj.TS_list{nst,6}{2,2} * 1e-6 * obj.TS_obs{r,9};
                                obj.tDataCellSelection(eventdata);
                            elseif c == 12 && eventdata.NewData == 0
                                nst = obj.TS_obs{r,14};
                                obj.TS_obs{r,17} = obj.TS_list{nst,6}{1,1} *1e-3 + obj.TS_list{nst,6}{1,2} * 1e-6 * obj.TS_obs{r,9};
                                obj.tDataCellSelection(eventdata);
                            end
                        case 2
                             obj.lev_obs{r,c} = eventdata.NewData;
                        case 3
                            obj.sp_obs{r,c} = eventdata.NewData;
                        case 4
                            obj.bl_obs{r,c} = eventdata.NewData;
                    end
                case 2 % results
                case 3 % coordinates
            end
        end
        
        % cancel adjustment
        function removeAdjustment(obj)
            if not(isempty(obj.TS_obs))
                obj.TS_obs(:,18:29) = cell(size(obj.TS_obs,1),12);  % remove computed a posteriori sigma and tests
            end
            if not(isempty(obj.TS_list))
                obj.TS_list(:,4:5) = cell(size(obj.TS_list,1),2);  % remove computed station correction
                obj.TS_list(:,7) = cell(size(obj.TS_list,1),1);    % remove a-posteriori h. inst. sigma
            end
            if not(isempty(obj.lev_obs))
                obj.lev_obs(:,5:9) = cell(size(obj.lev_obs,1),4);  % remove computed a posteriori sigma and tests
            end
            if not(isempty(obj.sp_obs))
                obj.sp_obs(:,13:24) = cell(size(obj.sp_obs,1),12);  % remove computed a posteriori sigma and tests
            end
            if not(isempty(obj.bl_obs))
                obj.bl_obs(:,14:25) = cell(size(obj.bl_obs,1),12);  % remove computed a posteriori sigma and tests
            end
            if not(isempty(obj.coord))
                obj.coord(:,[9:17 19:24]) = cell(size(obj.coord,1),15);     % remove computed a posteriori sigma and covariance matrix
                obj.coord(:,18) = {1};                             % set all the coordinates used for adjustment
            end
            obj.LSreport = [];                                     % remove all the statistic of the prevoius adjustment
            obj.adjusted = 0;                                      % set the variable to not adjusted
            set(obj.hh.bGraphic,'Enable','off')                   % disable the button for the graphic
            if ishandle(obj.gh)
                close(obj.gh);
            end
        end
        
        % function to create a new job
        function NewJob(obj)
            if obj.Saved == 0  % check if current data are saved
                savejob = questdlg('Do you want to save modify to the current job?'); % ask if one want to save the job
                % evaluate the decision of the user
                switch savejob     % evaluate the possiblity if the user doesn't try to cance operation
                    case 'Yes'     % if it yes save the job, also do nothing and go on to open another file
                        saveOK = obj.SaveData;
                        if saveOK == 0
                            return
                        end
                    case 'No'
                    case 'Cancel'  % if the user abort the operation stop the esecution of the function
                        return
                end
            end
            obj.SelectedEllips = 'GRS80'; % set current ellipsoid
            obj.SelectedGeoid = 'EGM08IT';
            obj.filename = [];
            obj.MaxIt = 10;           % Maximum number of iteration in LS adjustment
            obj.AdjType = 1;          % set the kind of adjustment chosed
            obj.Saved = 1;            % Flag to understand if some modifies were done from last saving (1 no modifies, 0 it is modified)
            obj.TS_obs = cell(0,29);  % cell array to store Total Station observations
            obj.TS_list = cell(0,6);  % list of the stations observed
            obj.bl_obs = cell(0,25);  % cell array to store baseline observations
            obj.sp_obs = cell(0,24);  % cell array to store single point observations
            obj.lev_obs = cell(0,9);  % cell array to store levelling observations
            obj.coord = cell(0,24);   % cell array to store coordinates observations
            obj.constraints = cell(0,9);   % variable to store constraints
            obj.LSreport = [];        % variable to store LS report
            set(obj.hh.bGraphic,'Enable','off');
            if ishandle(obj.gh)
                close(obj.gh);
            end
            obj.EmptyHome
            set(obj.hh.pVisualization,'SelectedObject',[]);
        end
        
        % function to re-open saved project
        function OpenData(obj)
            if obj.Saved == 0  % check if current data are saved
                savejob = questdlg('Do you want to save modify to the current job?'); % ask if one want to save the job
                % evaluate the decision of the user
                switch savejob     % evaluate the possiblity if the user doesn't try to cance operation
                    case 'Yes'     % if it yes save the job, also do nothing and go on to open another file
                        saveOK = obj.SaveData;
                        if saveOK == 0
                            return
                        end
                    case 'No'
                    case 'Cancel'  % if the user abort the operation stop the esecution of the function
                        return
                end
            end
            % get the file with ui interface
            [file, path] = uigetfile(strcat(obj.GUI_lastPath,'*.gnt'),'Open');
            if isempty(file) || ~exist(num2str([path file]),'file') % check if a file was chosen or not (if not the function was returned)
                return
            else obj.filename = strcat(path,file);
            end
            % load data
            loadData = load(obj.filename,'-mat');
            % store data in the right variable
            obj.ellipsoid = loadData.ellipsoid;
            obj.SelectedEllips = loadData.SelectedEllips;
            obj.adjusted = loadData.adjusted;
            obj.MaxIt = loadData.MaxIt;
            obj.geoid = loadData.geoid;
            obj.SelectedGeoid = loadData.SelectedGeoid;
            obj.TS_obs = loadData.TS_obs;
            obj.TS_list = loadData.TS_list;
            obj.bl_obs = loadData.bl_obs;
            obj.sp_obs = loadData.sp_obs;
            obj.lev_obs = loadData.lev_obs;
            obj.coord = loadData.coord;
            obj.AdjType = loadData.AdjType;
            obj.constraints = loadData.constraints;
            obj.LSreport = loadData.LSreport;
            obj.LSoptions = loadData.LSoptions;
            % set some parameters
            obj.Saved = 1;
            obj.GUI_lastPath = path;
            switch obj.adjusted
                case 0
                    obj.setVisualiPanel;                                        % set the home based on the previous given informations
                    set(obj.hh.bGraphic,'Enable','off');                       % enable the button for the graphic
                    if ishandle(obj.gh)
                        close(obj.gh);
                    end
                case 1
                    set(obj.hh.pVisualization,'SelectedObject',obj.hh.rCoord)   % set active the observation radio button
                    obj.GUI_typeVis = 3;                                        % set the visualization of coordinates
                    obj.GUI_coord = 2;
                    obj.setVisualiPanel;                                        % set the home based on the given informations
                    set(obj.hh.bGraphic,'Enable','on');                        % enable the button for the graphic
            end
        end
        
        % function to save with name the job
        function saveOK = SaveDataAs(obj)
            [file, path] = uiputfile(strcat(obj.GUI_lastPath,'*.gnt'),'Save current job as...');
            if isempty(file) || sum(file == 0) || sum(path == 0) % check if a file was chosen or not (if not the function was returned)
                saveOK = 0;
                return
            else obj.filename = strcat(path,file); % create the filename with the full path
            end
            % store data in the right variable
            ellipsoid = obj.ellipsoid;              %#ok<PROP,NASGU>
            SelectedEllips = obj.SelectedEllips;    %#ok<PROP,NASGU>
            adjusted = obj.adjusted;                %#ok<PROP,NASGU>
            MaxIt = obj.MaxIt;                      %#ok<PROP,NASGU>
            geoid = obj.geoid;                      %#ok<PROP,NASGU>
            SelectedGeoid = obj.SelectedGeoid;      %#ok<PROP,NASGU>
            TS_obs = obj.TS_obs;                    %#ok<PROP,NASGU>
            TS_list = obj.TS_list;                  %#ok<PROP,NASGU>
            bl_obs = obj.bl_obs;                    %#ok<PROP,NASGU>
            sp_obs = obj.sp_obs;                    %#ok<PROP,NASGU>
            lev_obs = obj.lev_obs;                  %#ok<PROP,NASGU>
            coord = obj.coord;                      %#ok<PROP,NASGU>
            AdjType = obj.AdjType;                  %#ok<PROP,NASGU>
            constraints = obj.constraints;          %#ok<PROP,NASGU>
            LSreport = obj.LSreport;                %#ok<PROP,NASGU>
            LSoptions = obj.LSoptions;              %#ok<PROP,NASGU>
            save(obj.filename,'ellipsoid','SelectedEllips','adjusted','MaxIt','geoid',...
                'SelectedGeoid','TS_obs','TS_list','bl_obs','sp_obs','lev_obs','coord','AdjType',...
                'constraints','LSreport','LSoptions');
            obj.Saved = 1;
            obj.GUI_lastPath = path;
            saveOK = 1;
        end
        
        % function to save when name is known
        function saveOK = SaveData(obj)
            if isempty(obj.filename)
                saveOK = obj.SaveDataAs;
            else
                ellipsoid = obj.ellipsoid;              %#ok<PROP,NASGU>
                SelectedEllips = obj.SelectedEllips;    %#ok<PROP,NASGU>
                adjusted = obj.adjusted;                %#ok<PROP,NASGU>
                MaxIt = obj.MaxIt;                      %#ok<PROP,NASGU>
                geoid = obj.geoid;                      %#ok<PROP,NASGU>
                SelectedGeoid = obj.SelectedGeoid;      %#ok<PROP,NASGU>
                TS_obs = obj.TS_obs;                    %#ok<PROP,NASGU>
                TS_list = obj.TS_list;                  %#ok<PROP,NASGU>
                bl_obs = obj.bl_obs;                    %#ok<PROP,NASGU>
                sp_obs = obj.sp_obs;                    %#ok<PROP,NASGU>
                lev_obs = obj.lev_obs;                  %#ok<PROP,NASGU>
                coord = obj.coord;                      %#ok<PROP,NASGU>
                AdjType = obj.AdjType;                  %#ok<PROP,NASGU>
                constraints = obj.constraints;          %#ok<PROP,NASGU>
                LSreport = obj.LSreport;                %#ok<PROP,NASGU>
                LSoptions = obj.LSoptions;              %#ok<PROP,NASGU>
                save(obj.filename,'ellipsoid','SelectedEllips','adjusted','MaxIt','geoid',...
                    'SelectedGeoid','TS_obs','TS_list','bl_obs','sp_obs','lev_obs','coord','AdjType',...
                    'constraints','LSreport','LSoptions');
                obj.Saved = 1;
                saveOK = 1;
            end
        end
        
        % function used in openfunction of txt option
        function openTXToption(obj,handles)
            obj.oh = handles;               % assign handles of the figure to the variable oh (other handles)
            % set all the popupmenu inactive
            for i = 1:13
                h = eval(['obj.oh.field' num2str(i)]);
                set(h,'String',' ','Value',1,'Enable','off');
            end
            % set the popup menu active with the rigth field value (the
            % last one chosen)
            for i = 1:obj.n_field
                h = eval(['obj.oh.field' num2str(i)]);
                set(h,'String',obj.txt_field,'Value',obj.txt_order(i),'Enable','on');
            end
            % set the rigth delimiter (last one chosen)
            set(obj.oh.pDelimiter,'Value',obj.txt_del,'Enable','on');
            % set the rigth delimiter (last one chosen)
            set(obj.oh.eHeader,'String',num2str(obj.txt_header),'Enable','on');
        end
        
        % function to get the txt option when the txt option figure is
        % closed
        function closeTXToption(obj)
            % get the order of the fields
            txt_ord = nan(obj.n_field,1);
            for i = 1 : obj.n_field
                h = eval(['obj.oh.field' num2str(i)]);
                txt_ord(i) = get(h,'Value');
            end
            if length(unique(txt_ord)) == obj.n_field % check if fields are repeted only once
                obj.txt_order = txt_ord;
                if  ~isnan(str2double(get(obj.oh.eHeader,'String'))) % check if delimiter is a number
                    % get the number of headerlines
                    obj.txt_header = str2double(get(obj.oh.eHeader,'String'));
                    % get the chosen delimiter
                    obj.txt_del = get(obj.oh.pDelimiter,'Value');
                    % close the figure
                    close(obj.oh.fTXToption);
                else msgbox('Set numerical value in number of headerlines','Error','error') % error if there is no numerica numb of headerlines
                end % end of second if
            else msgbox('A field was chosen double','Error','error') % error if there is a field repeated more than once
            end
            
            
        end
        
        % lock home
        function lockHome(obj)
            h = fieldnames(obj.hh);
            obj.prevHstatus = cell(size(h));
            for i = 1:size(h,1)
                prop = get(obj.hh.(h{i}));
                if sum(strcmp('Enable',fieldnames(prop)))~=0
                    obj.prevHstatus{i,1} = get(obj.hh.(h{i}),'Enable');
                    set(obj.hh.(h{i}),'Enable','off');
                else
                    obj.prevHstatus{i,1} = 'noEnable';
                end
            end
        end
        
        % unlock home
        function unlockHome(obj)
            h = fieldnames(obj.hh);
            for i = 1:size(h,1)
                if ~strcmp(obj.prevHstatus{i},'noEnable')
                    set(obj.hh.(h{i}),'Enable',obj.prevHstatus{i});
                end
            end
        end
        
        % open graphic
        function viewGraphics(obj, scale)
            XYZ = cell2mat(obj.coord(:,2:4));
            XYZo = mean(XYZ,1);
            a = obj.ellipsoid.WGS84.a;
            f = obj.ellipsoid.WGS84.f;
            Cxx = cell2mat(obj.coord(:,12:17));
            Cxx = mat2cell(Cxx,ones(size(Cxx,1),1),size(Cxx,2));
            Cxx = cellfun(@(x) [x(1) x(2) x(3); x(2) x(4) x(5); x(3) x(5) x(6)], Cxx, 'UniformOutput', 0);
            [ENU, Cee, ~, ~] = obj.xyz2enu(XYZ, a, f, XYZo, Cxx);
            obj.gh = figure;
            set(obj.gh,'NumberTitle','off','Name','GeoNet: Graphic')
            plot(ENU(:,1), ENU(:,2), 'r+'); grid on;
            axis equal
            hold on;
            for i = 1:size(ENU,1)
                if max(abs(Cee{i}(:)))>1e-10
                    error_ellipse(Cee{i}(1:2,1:2),ENU(i,1:2)./scale,'scale',scale)
                end
                text(ENU(i,1),ENU(i,2),{obj.coord{i,1};[sprintf('%.2f',obj.coord{i,7}) ' m']},'HorizontalAlignment','left','VerticalAlignment','Middle');
            end
        end
    end % end of methods of home GUI

        
    
    % Import GUI functions %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    methods
        % Opening GUI function for Total Station
        function OpenImportTSobs(obj, handles)
            obj.ih = handles;                         % Assign the handles of the figure to the variable ih
            obj.txt_del = 1;                          % Set the chose delimiter
            obj.txt_order = 1:8;                      % Set default txt freields order for importation
            obj.n_field = length(obj.txt_order);      % Set the total numeber of fields
            obj.txt_header = 0;                       % Set txt fields header (number of line of header of the txt file to import)
            obj.txt_field = {'Station name';
                'Point name';
                'Station instrumental height';
                'Azimut (gon)';
                'Zenith (gon)';
                'Distance (m)';
                'Prism heigth';
                'Point code'};           % set the list for popup menu of tx option
            obj.fileImp = [];                         % Clear file name variable
            set(obj.ih.ImportObs,'Name','Import total station observations')   % set the title of the mask
            set(obj.ih.pInstrPrecTS,'Visible','on')   % set the panel TS precision to visible
            set(obj.ih.pInstrHAccu,'Visible','on')    % set the panel Instrumental heigth accuracy to visible
            set(obj.ih.pInstrPrecLev,'Visible','off') % set the panels Levelling precision not visible
            set(obj.ih.pInstrPrecGPS,'Visible','off') % set the panels GPS precision not visible
            set(obj.ih.pmFileType,'String',...        % set string value in the pop-up menu where one chose the kind of file
                ['Select file type...|ASCII file (*.txt)|Leica GeoOffice ' ...
                'custom exportation file (*.cst)|Topcon MemoTop2 (*.mm2)'])
            set(obj.ih.bOptions,'Enable','off');      % set button options off
            set(obj.ih.pAzDir,'Visible','on');        % set the panel with the azimutal direction on
            % Disable clockwise setting (they will be used only if they
            % will be needed)
            set(obj.ih.rCW,'Enable','off','Value',1)
            set(obj.ih.rCCW,'Enable','off')
            % Create the list of file type for total station
            obj.typeList = [];                                 % initializing the variable
            obj.typeList{1,1} = '*.txt';                       % txt file
            obj.typeList{1,2} = '*.cst';                       % Leica custom file
            obj.typeList{1,3} = '*.mm2';                       % Topcon mm2
        end % end of OpenImportTSobs
        
        % Opening GUI function for Levelling observations
        function OpenImportLEVobs(obj, handles)
            obj.ih = handles;                                           % Assign the handles of the figure to the variable ih
            obj.txt_del = 1;                                            % Set the chose delimiter
            obj.txt_order = 1:4;                                        % Set default txt fields order for importation
            obj.n_field = length(obj.txt_order);                        % Set the total numeber of fields
            obj.txt_header = 0;                                         % Set txt fields header (number of line of header of the txt file to import)
            obj.txt_field = {'Backsight point';
                'Foresight point';
                'Height difference (m)';
                'Distance between point (m)'};                 % set the list for popup menu of txt option
            obj.fileImp = [];                                           % Clear file name variable
            set(obj.ih.ImportObs,'Name','Import levelling observations')% set the title of the panel
            set(obj.ih.pInstrHAccu,'Visible','off')                      % set the panel Instrumental heigth accuracy to not visible
            set(obj.ih.pInstrPrecTS,'Visible','off')                    % set the panel TS precision to not visible
            set(obj.ih.pInstrPrecLev,'Visible','on')                    % set the panels Levelling precision to visible
            set(obj.ih.pInstrPrecGPS,'Visible','off')                   % set the panels GPS precision to not visible
            set(obj.ih.pmFileType,'String',...              % set string value in the pop-up menu where one chose the kind of file
                'Select file type...|ASCII file (*.txt)')
            set(obj.ih.bOptions,'Enable','off');                        % set button options off
            set(obj.ih.pAzDir,'Visible','off');                         % set the panel with the azimutal direction off (It's used only for TS)
            % Create the list of file type for total station
            obj.typeList = [];                                          % initializing the variable
            obj.typeList{1,1} = '*.txt';                                % txt file
        end
        
        % Opening GUI function for GPS observations
        function OpenImportGPSobs(obj, handles)
            obj.ih = handles;                         % Assign the handles of the figure to the variable ih
            obj.txt_del = 1;                          % Set the chosen delimiter
            obj.txt_order = 1:12;                     % Set default txt fields order for importation
            obj.n_field = length(obj.txt_order);      % Set the total numeber of fields
            obj.txt_header = 0;                       % Set txt fields header (number of line of header of the txt file to import)
            obj.txt_field = {'Point name';
                'X (m)';
                'Y (m)';
                'Z (m)';
                'Point code';
                'M0';
                'Cofactor matrix (Qxx)';
                'Cofactor matrix (Qxy)';
                'Cofactor matrix (Qxz)';
                'Cofactor matrix (Qyy)';
                'Cofactor matrix (Qyz)';
                'Cofactor matrix (Qzz)'};% set the list for popup menu of tx option
            obj.fileImp = [];                         % Clear file name variable
            set(obj.ih.ImportObs,'Name','Import GPS master station observations')   % set the title of the panel
            set(obj.ih.pInstrHAccu,'Visible','off')                     % set the panel Instrumental heigth accuracy to not visible
            set(obj.ih.pInstrPrecTS,'Visible','off')  % set the panel TS precision to not visible
            set(obj.ih.pInstrPrecLev,'Visible','off') % set the panels Levelling precision not visible
            set(obj.ih.pInstrPrecGPS,'Visible','on') % set the panels GPS precision to visible
            set(obj.ih.pmFileType,'String',...        % set string value in the pop-up menu where one chose the kind of file
                ['Select file type...|ASCII file (*.txt)|Leica GeoOffice ' ...
                'custom exportation file (*.cst)'])
            set(obj.ih.bOptions,'Enable','off');      % set button options off
            set(obj.ih.pAzDir,'Visible','off');                          % set the panel with the azimutal direction off (It's used only for TS)
            % Create the list of file type for GPS single point
            obj.typeList = [];                                 % initializing the variable
            obj.typeList{1,1} = '*.txt';                       % txt file
            obj.typeList{1,2} = '*.cst';                       % Leica custom file
        end
        
        % Opening GUI function for GPS baseline observations
        function OpenImportBLobs(obj, handles)
            obj.ih = handles;                         % Assign the handles of the figure to the variable ih
            obj.txt_del = 1;                          % Set the chosen delimiter
            obj.txt_order = 1:13;                     % Set default txt fields order for importation
            obj.n_field = length(obj.txt_order);      % Set the total numeber of fields
            obj.txt_header = 0;                       % Set txt fields header (number of line of header of the txt file to import)
            obj.txt_field = {'Station name';
                'Point name';
                'X Difference (m)';
                'Y Difference (m)';
                'Z Difference (m)';
                'Point code';
                'M0';
                'Cofactor matrix (Qxx)';
                'Cofactor matrix (Qxy)';
                'Cofactor matrix (Qxz)';
                'Cofactor matrix (Qyy)';
                'Cofactor matrix (Qyz)';
                'Cofactor matrix (Qzz)'};% set the list for popup menu of tx option
            obj.fileImp = [];                         % Clear file name variable
            set(obj.ih.ImportObs,'Name','Import GPS master station observations')   % set the title of the panel
            set(obj.ih.pInstrHAccu,'Visible','off')                      % set the panel Instrumental heigth accuracy to not visible
            set(obj.ih.pInstrPrecTS,'Visible','off')  % set the panel TS precision to not visible
            set(obj.ih.pInstrPrecLev,'Visible','off') % set the panels Levelling precision not visible
            set(obj.ih.pInstrPrecGPS,'Visible','on') % set the panels GPS precision to visible
            set(obj.ih.pmFileType,'String',...        % set string value in the pop-up menu where one chose the kind of file
                ['Select file type...|ASCII file (*.txt)|Leica GeoOffice ' ...
                'custom exportation file (*.cst)'])
            set(obj.ih.bOptions,'Enable','off');      % set button options off
            set(obj.ih.pAzDir,'Visible','off');       % set the panel with the azimutal direction off (It's used only for TS)
            % Create the list of file type for GPS single point
            obj.typeList = [];                                 % initializing the variable
            obj.typeList{1,1} = '*.txt';                       % txt file
            obj.typeList{1,2} = '*.cst';                       % Leica custom file
        end
        
        % Opening GUI function for Approximated coordinates
        function OpenImportCoord(obj,handles)
            obj.ih = handles;                         % Assign the handles of the figure to the variable ih
            obj.txt_del = 1;                          % Set the chosen delimiter
            obj.txt_order = 1:5;                      % Set default txt fields order for importation
            obj.n_field = length(obj.txt_order);      % Set the total numeber of fields
            obj.txt_header = 0;                       % Set txt fields header (number of line of header of the txt file to import)
            obj.txt_field = {'Point name';
                'X (m)';
                'Y (m)';
                'Z (m)';
                'Point code'};           % set the list for popup menu of tx option
            
            obj.fileImp = [];                         % Clear file name variable
            switch obj.CoordImpType
                case 1
                    set(obj.ih.fImportCoord,'Name','Import approximated coordinates')   % set the title of the panel
                    obj.lockHome;
                case 2
                    set(obj.ih.fImportCoord,'Name','Import constraints')   % set the title of the panel
                    obj.lockConstraints;
            end
            % Create the list of file type for Approximated coordinates
            obj.typeList = [];                                 % initializing the variable
            obj.typeList{1,1} = '*.txt';                       % txt file
            % set the popup menu active with the rigth field value (the
            % last one chosen)
            for i = 1:obj.n_field
                h = eval(['obj.ih.field' num2str(i)]);
                set(h,'String',obj.txt_field,'Value',obj.txt_order(i),'Enable','on');
            end
            % set the rigth delimiter (last one chosen)
            set(obj.ih.pDelimiter,'Value',obj.txt_del,'Enable','on');
            % set the rigth delimiter (last one chosen)
            set(obj.ih.eHeader,'String',num2str(obj.txt_header),'Enable','on');
            % set active Cartesian geocentric coordinates
            set(obj.ih.pCoordType,'SelectedObject',obj.ih.rCartesian);
            % verify which coordinates are possible to use and allow only
            % them
            switch obj.AdjType
                case {3, 4}
                    set(obj.ih.rGeodSex,'Enable','off');
                    set(obj.ih.rGeodDec,'Enable','off');
            end
        end
        
        % get name of the file to import (for all type of observations)
        % it need as imput the row of the type of file list (0 for no file
        % selction)
        function getFileName(obj, ftype)
            if ftype ~= 0 % check if a file type was chosen
                [fname, pathname] = uigetfile(...                                                % open GUI to select the file in the last folder used
                    strcat(obj.GUI_lastPath,obj.typeList{1,ftype}),'Select file to import...');  % searching for files of the type selected
                if ischar(pathname) && ischar(fname)     % check if no file was selected
                    obj.GUI_lastPath = pathname;                                                 % save the last path used in order to recall it
                    obj.fileImp = strcat(pathname,fname);                                        % save the full path filename in its variable
                    set(obj.ih.tFilePath,'HorizontalAlignment','left')                           % align the text on the left
                    set(obj.ih.tFilePath,'String',obj.fileImp);                                  % Visulize the full path on the mask
                else
                    set(obj.ih.tFilePath,'HorizontalAlignment','left')                           % align the text on the left
                    set(obj.ih.tFilePath,'String','Select file ...')                             % Visulize choose file in pathname space
                    obj.fileImp = [];                                                            % Set the name of the file to import empty
                end
            else obj.lockImport
                obj.oh =  msgbox('Chose a file type','Warning','warn');                          % if no type of file is selected view error message
                set(obj.oh,'CloseRequestFcn',@obj.CloseImportMsg)                                % change closefunction
                chil = get(obj.oh,'Children');                                                   % get children
                for i = 1:length(chil)
                    if strcmp(get(chil(i),'Type'),'uicontrol') && strcmp(get(chil(i),'Tag'),'OKButton') % change callback ok OK Button
                        set(chil(i),'Callback',@obj.CloseImportMsg);
                    end
                end
                set(obj.ih.tFilePath,'HorizontalAlignment','left')                               % align the text on the left
                set(obj.ih.tFilePath,'String','Select file...');                                 % Visulize choose file  in pathname space
                obj.fileImp = [];                                                                % Set the name of the file to import empty
            end
        end % end of getFileName
        
        % Operation done after selecting file type (for all type of
        % observations)
        function setfiletype(obj)
            ftype = get(obj.ih.pmFileType,'Value') - 1;    % Get the row of the selected file type (-1 is because first row is select file type)
            switch ftype
                case 1 % for txt files it's necessary to activate button Options
                    set(obj.ih.bOptions,'Enable','on');   % set button on
                    % Enable clockwise choice
                    set(obj.ih.rCW,'Enable','on','Value',1)
                    set(obj.ih.rCCW,'Enable','on')
                otherwise
                    set(obj.ih.bOptions,'Enable','off');  % set button off
                    % Disable clockwise choice
                    set(obj.ih.rCW,'Enable','off','Value',1)
                    set(obj.ih.rCCW,'Enable','off')
            end
        end % end of setfiletype
        
        % set the type of coordinates to import
        function setCoordtype(obj)
            switch get(obj.ih.pCoordType,'SelectedObject');       % understand which kind of coordinates is selected
                case obj.ih.rCartesian % Cartesian gecentric coordinates
                    obj.txt_field = {'Point name';
                        'X (m)';
                        'Y (m)';
                        'Z (m)';
                        'Point code'};                       % set the list for popup menu of tx option
                case obj.ih.rGeodSex % Sexagesimal geodetic coordinates
                    obj.txt_field = {'Point name';
                        'Latitude';
                        'Longitude';
                        'h (m)';
                        'Point code'};                       % set the list for popup menu of tx option
                case obj.ih.rGeodDec % Sexadecimal geodetic coordinates
                    obj.txt_field = {'Point name';
                        'Latitude';
                        'Longitude';
                        'h (m)';
                        'Point code'};                       % set the list for popup menu of tx option
            end
            % set the popup menu of the field to the set values, based on
            % the coordinates type chosen
            for i = 1:obj.n_field
                h = eval(['obj.ih.field' num2str(i)]);
                set(h,'String',obj.txt_field,'Value',obj.txt_order(i),'Enable','on');
            end
        end
        
        % Import TS data
        function importTSdata(obj)
            ftype = get(obj.ih.pmFileType,'Value') - 1;    % Get the row of the selected file type (-1 is because first row is select file type)
            % collect all the parameters of the instrument accuracy in one
            % variable
            sigma = {str2double(get(obj.ih.eSigmaD,'String')),   str2double(get(obj.ih.ePpmD,'String'));
                str2double(get(obj.ih.eSigmaDnp,'String')), str2double(get(obj.ih.eDnpPpm,'String'));
                str2double(get(obj.ih.eSigmaZ,'String')),   [];
                str2double(get(obj.ih.eSigmaA,'String')),   [];
                str2double(get(obj.ih.eSigmaHs,'String')) / 10^3,   []};
            
            if isnan(sigma{1,1}) || isnan(sigma{2,1}) || isnan(sigma{3,1}) ||...      % check if some terms of the variance are wrong or missed
                    isnan(sigma{4,1}) || isnan(sigma{1,2}) || isnan(sigma{2,2}) ||... % check if some terms of the variance are wrong or missed
                    isnan(sigma{5,1}) || isempty(obj.fileImp)                         % check if a file is selected
                msgbox('Missing instrument accuracy or file name','Warning','error')  % if they are wrong give back error message
            elseif  (sigma{1,1} == 0 && sigma{1,1} == 0) ||...              % verify thath there are not zeros elements in the variances
                    (sigma{2,1} == 0 && sigma{2,1} == 0) ||...
                    sigma{3,1} == 0 || sigma{4,1} == 0 || sigma{5,1} == 0
                msgbox('It is not possible to insert instrumental accuracy equal to 0','Warning','error')  % if they are wrong give back error message
            else
                switch ftype
                    case 0 % no file chosen - error message
                        msgbox('No file type chosen','Warning','warn')
                    case 1 % txt file
                        [obs, list] = obj.importTStxt;
                    case 2 % LGO file
                        [obs, list] = obj.importLeicaTS;
                    case 3 % mm2 file
                        [obs, list] = obj.importMM2;
                end % end of switch
                dim = size(obj.TS_obs,2);
                obs(:,15:dim) = cell(size(obs,1),dim-15+1);                              % allocate space for variance and tests results
                list(:,3:7) = cell(size(list,1),5);                               % initialize empty column of TS_list
                % create the column of a priori known instrumental error
                idx = cellfun(@isempty,obs(:,5));      % find empty azimut
                if ~isempty(obs(~idx,5))               % do the opertion only if there are some azimuth
                    obs(~idx,15) = sigma(4,1);         % import Azimutal apriori error
                    if get(obj.ih.pAzDir,'SelectedObject') == obj.ih.rCCW   % check if  azimutal direction is CCW
                        obs(idx,5) = mat2cell(400 - cell2mat(obs(idx,5)));  % if it is change direction of the azimut (they will be clockwise by definition
                    end
                end
                idx = cellfun(@isempty,obs(:,7));      % find empty zenith
                if ~isempty(obs(~idx,16))              % do the opertion only if there are some zenith
                    obs(~idx,16) = sigma(3,1);         % import Zenital apriori error
                end
                idx_full = cellfun(@isempty,obs(:,9));                                 % find empty distance
                idx_pr = ~cell2mat(obs(:,12)) & ~idx_full;                             % find prism measured distance
                idx_np =  cell2mat(obs(:,12)) & ~idx_full;                             % find no prism measured distance
                sDpr = cell2mat(obs(idx_pr,9)).*10^-6.*sigma{1,2}+10^-3.*sigma{1,1};   % compute apriori error for prism distance (it is a matrix)
                sDnp = cell2mat(obs(idx_np,9)).*10^-6.*sigma{2,2}+10^-3.*sigma{2,1};   % compute apriori error for no prism distance (it is a matrix)
                if ~isempty(obs(idx_pr,9))                                             % do the opertion only if there are some prism distance
                    obs(idx_pr,17) = num2cell(sDpr);                                   % assign apriori error for prism distance
                end
                if ~isempty(obs(idx_np,9))                                             % do the opertion only if there are some no prism distance
                    obs(idx_np,17) = num2cell(sDnp);                                   % assign apriori error for no prism distance
                end
                list(:,6) = {sigma};                                                   % save sigma in each row of the list variable (now it's possible to know always these value for each station
                list(:,3) = sigma(5,1);
                % store data in variables
                % check if existing data must or not be replaced
                if get(obj.ih.rAdd,'Value') == 1  % Adding to existing data is required
                    if isempty(obj.TS_obs) % check if no data is stored yet
                        % store data if no data is stored yet
                        obj.TS_obs = obs;
                        obj.TS_list = list;
                    else % if data are stored yet
                        stn_num = cell2mat(obs(:,14)) + size(obj.TS_list,1); % change the number of station in the 14th column (by adding the number of station already present)
                        obs(:,14) = num2cell(stn_num);                       % store new station number (conversion from matrix is needed)
                        obj.TS_obs = [obj.TS_obs; obs];                      % store variable by adding them to existing one
                        obj.TS_list = [obj.TS_list; list];                   % store variable by adding them to existing one
                    end
                elseif get(obj.ih.rOver,'Value') == 1 % Overwriting existing data
                    obj.TS_obs = obs;
                    obj.TS_list = list;
                end
                
                % close the figure and set visualization
                close(obj.ih.ImportObs);                                    % close the figure
                drawnow
                obj.removeAdjustment;                                       % cancel the previous adjustment
                obj.Saved = 0;                                              % set saved to 0
                set(obj.hh.pVisualization,'SelectedObject',obj.hh.rObs)     % set active the observation radio button
                obj.GUI_typeVis = 1;                                        % set the visualization of observation
                obj.GUI_typeObsRes = 1;                                     % set the kind of observation
                obj.setVisualiPanel;                                        % set the home based on the given informations
            end % end of if
        end % end of import TS data
        
        % Import Levelling data
        function importLEVdata(obj)
            ftype = get(obj.ih.pmFileType,'Value') - 1;    % Get the row of the selected file type (-1 is because first row is select file type)
            % collect all the parameters of the instrument accuracy in one
            % variable
            sigma = {str2double(get(obj.ih.eSigmaDh,'String')),   str2double(get(obj.ih.ePpmDh,'String'))};
            if isnan(sigma{1,1}) || isnan(sigma{1,2}) ||  isempty(obj.fileImp)    % check if a file is selected or if some component of sigma aren't numeric
                msgbox('Missing instrument accuracy or file name','Warning','error')   % if they are wrong give back error message
            else
                switch ftype
                    case 0 % no file chosen - error message
                        msgbox('No file type chosen','Warning','warn')
                    case 1 % import from txt file
                        obs = obj.importLEVtxt(sigma);
                end % end of switch
                dim = size(obj.lev_obs,2);
                obs(:,6:dim) = cell(size(obs,1),dim-6+1);                              % allocate space for variance and tests results 
                % store data in variables
                % check if existing data must or not be replaced
                if get(obj.ih.rAdd,'Value') == 1  % Adding to existing data is required
                    if isempty(obj.lev_obs) % check if no data is stored yet
                        % store data if no data is stored yet
                        obj.lev_obs = obs;
                    else % if data are stored yet
                        obj.lev_obs = [obj.lev_obs; obs];                      % store variable by adding them to existing one
                    end
                elseif get(obj.ih.rOver,'Value') == 1 % Overwriting existing data
                    obj.lev_obs = obs;
                end
                
                % close the figure and set visualization
                close(obj.ih.ImportObs);                                    % close the figure
                drawnow
                pause(0.001);
                obj.removeAdjustment;                                       % cancel the previous adjustment
                obj.Saved = 0;                                              % set saved to 0
                set(obj.hh.pVisualization,'SelectedObject',obj.hh.rObs)     % set active the observation radio button
                obj.GUI_typeVis = 1;                                        % set the visualization of observation
                obj.GUI_typeObsRes = 2;                                     % set the kind of observation
                obj.setVisualiPanel;                                        % set the home based on the given informations
                
            end % end of switch
        end % end of function importLEVdata
        
        % Import GPS single point data
        function importGPSdata(obj)
            ftype = get(obj.ih.pmFileType,'Value') - 1;    % Get the row of the selected file type (-1 is because first row is select file type)
            % collect all the parameters of the instrument accuracy in one
            % variable
            if isempty(obj.fileImp)    % check if a file is selected or if some component of sigma aren't numeric
                msgbox('Missing file name','Warning','error')   % if they are wrong give back error message
            else
                switch ftype
                    case 0 % no file chosen - error message
                        msgbox('No file type chosen','Warning','warn')
                    case 1 % import from txt file
                        obs = obj.importGPStxt;
                    case 2 % import from Leica CST file
                        obs = obj.importGPScst;
                end % end of switch
                dim = size(obj.sp_obs,2);
                obs(:,13:dim) = cell(size(obs,1),dim-13+1); % initialize empty column of obs
                % store data in variables
                % check if existing data must or not be replaced
                if get(obj.ih.rAdd,'Value') == 1  % Adding to existing data is required
                    if isempty(obj.sp_obs) % check if no data is stored yet
                        % store data if no data is stored yet
                        obj.sp_obs = obs;
                    else % if data are stored yet
                        obj.sp_obs = [obj.sp_obs; obs];                      % store variable by adding them to existing one
                    end
                elseif get(obj.ih.rOver,'Value') == 1 % Overwriting existing data
                    obj.sp_obs = obs;
                end
                
                % close the figure and set visualization
                obj.removeAdjustment;                                       % cancel the previous adjustment
                obj.Saved = 0;                                              % set saved to 0
                set(obj.hh.pVisualization,'SelectedObject',obj.hh.rObs)     % set active the observation radio button
                obj.GUI_typeVis = 1;                                        % set the visualization of observation
                obj.GUI_typeObsRes = 3;                                     % set the kind of observation
                obj.setVisualiPanel;                                        % set the home based on the given informations
                close(obj.ih.ImportObs);                                    % close the figure
            end % end of switch
        end
        
        % Import GPS baseline data
        function importBLdata(obj)
            ftype = get(obj.ih.pmFileType,'Value') - 1;    % Get the row of the selected file type (-1 is because first row is select file type)
            % collect all the parameters of the instrument accuracy in one
            % variable
            if isempty(obj.fileImp)    % check if a file is selected or if some component of sigma aren't numeric
                msgbox('Missing file name','Warning','error')   % if they are wrong give back error message
            else
                switch ftype
                    case 0 % no file chosen - error message
                        msgbox('No file type chosen','Warning','warn')
                    case 1 % import from txt file
                        obs = obj.importBLtxt;
                    case 2 % import from Leica CST file
                        obs = obj.importBLcst;
                end % end of switch
                dim = size(obj.bl_obs,2);
                obs(:,14:dim) = cell(size(obs,1),dim-14+1); % initialize empty column of obs
                % store data in variables
                % check if existing data must or not be replaced
                if get(obj.ih.rAdd,'Value') == 1  % Adding to existing data is required
                    if isempty(obj.bl_obs) % check if no data is stored yet
                        % store data if no data is stored yet
                        obj.bl_obs = obs;
                    else % if data are stored yet
                        obj.bl_obs = [obj.bl_obs; obs];                      % store variable by adding them to existing one
                    end
                elseif get(obj.ih.rOver,'Value') == 1 % Overwriting existing data
                    obj.bl_obs = obs;
                end
                
                % close the figure and set visualization
                obj.removeAdjustment;                                       % cancel the previous adjustment
                obj.Saved = 0;                                              % set saved to 0
                set(obj.hh.pVisualization,'SelectedObject',obj.hh.rObs)     % set active the observation radio button
                obj.GUI_typeVis = 1;                                        % set the visualization of observation
                obj.GUI_typeObsRes = 4;                                     % set the kind of observation
                obj.setVisualiPanel;                                        % set the home based on the given informations
                close(obj.ih.ImportObs);                                    % close the figure
            end % end of switch
        end
        
        % Import approximated coordinates
        function importCoordApp(obj)
            % get the order of the fields
            txt_ord = nan(obj.n_field,1);
            for i = 1 : obj.n_field
                h = eval(['obj.ih.field' num2str(i)]);
                txt_ord(i) = get(h,'Value');
            end
            if length(unique(txt_ord)) == obj.n_field % check if fields are repeted only once
                obj.txt_order = txt_ord;
                if  ~isnan(str2double(get(obj.ih.eHeader,'String'))) % check if delimiter is a number
                    % get the number of headerlines
                    obj.txt_header = str2double(get(obj.ih.eHeader,'String'));
                    % get the chosen delimiter
                    obj.txt_del = get(obj.ih.pDelimiter,'Value');
                    % import coordinates from txt file
                    txt_in = obj.importTXT(obj.fileImp,obj.txt_del_list{obj.txt_del}...
                        ,obj.txt_order,obj.txt_header);
                    % allocate space for coordinates variable
                    coord_in = cell(size(txt_in,1),24);
                    % assign name and point code to the final variable
                    coord_in(:,[1 8]) = txt_in(:,[1 5]);
                    % get ellipsoid parameters
                    ellips = getfield(obj.ellipsoid,obj.SelectedEllips); %#ok<GFLD>
                    a = ellips.a;       % majoer semiaxis
                    f = ellips.f;   % flattening
                    % check wich type of coordinates is selected
                    switch get(obj.ih.pCoordType,'SelectedObject');
                        case obj.ih.rCartesian % Cartesian gecentric coordinates
                            coord_in(:,[2 3 4]) = num2cell(cellfun(@str2num,txt_in(:,[2 3 4])));     % store imported values
                            XYZ_mat = cell2mat(coord_in(:,[2 3 4]));                       % convert XYZ into a matrix
                            flh_mat = obj.cart2geo(XYZ_mat, a, f);                         % extract geodetic coordinates in radians
                            flh_mat(:,1:2) = flh_mat(:,1:2) .* 180 ./ pi;                  % convert angle to sexadecimal degrees
                            coord_in(:,[5 6 7]) = num2cell(flh_mat);                       % store geodetic coordinates
                            obj.GUI_coord = 2;                                             % set the kind of coordinates to visulize (cartesian)
                        case obj.ih.rGeodSex % Sexagesimal geodetic coordinates
                            coord_in(:,[5 6]) = num2cell(cellfun(@obj.sex2dec,txt_in(:,[2 3])));     % store imported values (angles), converting them into sexadecimal
                            coord_in(:,7) = num2cell(cellfun(@str2num,txt_in(:,4)));                 % store imported heigth values
                            flh_mat = cell2mat(coord_in(:,[5 6 7]));                       % convert flh into a matrix
                            flh_mat(:,1:2) = flh_mat(:,1:2) .* pi ./ 180;                  % convert angle to radians
                            XYZ_mat = obj.geo2cart(flh_mat, a, f);                         % extract geodetic coordinates in radians
                            coord_in(:,[2 3 4]) = num2cell(XYZ_mat);                       % store cartesian geocentric coordinates
                            obj.GUI_coord = 1;                                             % set the kind of coordinates to visulize (geodetic)
                        case obj.ih.rGeodDec % Sexadecimal geodetic coordinates
                            coord_in(:,[5 6 7]) = num2cell(cellfun(@str2num,txt_in(:,[2 3 4])));     % store imported values
                            flh_mat = cell2mat(coord_in(:,[5 6 7]));                       % convert flh into a matrix
                            flh_mat(:,1:2) = flh_mat(:,1:2) .* pi ./ 180;                  % convert angle to radians
                            XYZ_mat = obj.geo2cart(flh_mat, a, f);                         % extract geodetic coordinates in radians
                            coord_in(:,[2 3 4]) = num2cell(XYZ_mat);                       % store cartesian geocentric coordinates
                            obj.GUI_coord = 1;                                             % set the kind of coordinates to visulize (geodetic)
                    end
                    % store data in variables
                    % check if existing data must or not be replaced
                    if get(obj.ih.rAdd,'Value') == 1  % Adding to existing data is required
                        if isempty(obj.coord) % check if no data is stored yet
                            % store data if no data is stored yet
                            obj.coord = coord_in;
                        else % if data are stored yet
                            obj.coord = [obj.coord; coord_in];                      % store variable by adding them to existing one
                        end
                    elseif get(obj.ih.rOver,'Value') == 1 % Overwriting existing data
                        obj.coord = coord_in;
                    end
                    % close the figure
                    close(obj.ih.fImportCoord);
                    obj.removeAdjustment;
                    obj.Saved = 0;                                              % set saved to 0
                    set(obj.hh.pVisualization,'SelectedObject',obj.hh.rCoord)   % set active the observation radio button
                    obj.GUI_typeVis = 3;                                        % set the visualization of observation
                    obj.setVisualiPanel;                                        % set the home based on the given informations
                else msgbox('Set numerical value in number of headerlines','Error','error') % error if there is no numerica numb of headerlines
                end % end of second if
            else msgbox('A field was chosen double','Error','error') % error if there is a field repeated more than once
            end
        end
        
        % import generic txt file for TS
        function [TS_obs, TS_list] = importTStxt(obj)
            txt_in = obj.importTXT(obj.fileImp,obj.txt_del_list{obj.txt_del}...
                ,obj.txt_order,obj.txt_header);                                     % import txt file based on the option set
            TS_obs(:,[1 2 3 5 7 9 10 13]) = txt_in;                                 % reassign the file to the variable TS_obs in the right order
            TS_obs(:,[3 5 7 9 10]) = cellfun(@str2num,...
                TS_obs(:,[3 5 7 9 10]),'UniformOutput',false);                      % convert format of column that are numbers (string to numbers)
            TS_obs(:,[4 6 8]) = num2cell(~cellfun(@isempty,TS_obs(:,[5 7 9])));     % set active all the measurement done
            TS_obs(:,11) = num2cell( cell2mat(TS_obs(:,7)) > 200);                  % find left circle measurement (the ones that have zenith grather than 200)
            TS_obs(:,12) = num2cell(~cellfun(@isempty,strfind(TS_obs(:,13),'NP'))); % find no prism measuremente by identifying the character NP in the point code
            TS_name_list = unique(TS_obs(:,1));                                     % find list of station names
            
            % loop to rename all the stations with numeric values
            PT_ren = nan(size(TS_obs,1),2);                       % initialize the variable where all the rows with renamed station are saved
            for i = 1 : size(TS_name_list,1)
                TS_pos =  strcmp(TS_obs(:,1),TS_name_list(i));    % extract the rows measured by the station at position i
                PT_ren(TS_pos,1) = i .* ones(sum(TS_pos),1);   % rename the station with the iteration number
                PT_ren(TS_pos,2) = cell2mat(TS_obs(TS_pos,3));    % save the station heigth near the
            end
            
            [TS_list, idx_list, idx_obs] = unique(PT_ren,'rows');                   % find unique value between station and their indexes
            TS_list = num2cell(TS_list);
            TS_obs(:,14) = num2cell(idx_obs);                                       % associate to each row the progressive number of the station
            TS_list(:,1) = TS_obs(idx_list,1);                                      % change name of stations from numbers to strings        
        end % end of importTStxt
        
        % function to import LEICA CST file generated with the model given
        % with GeoNet
        function [TS_obs, TS_list] = importLeicaTS(obj)
            cst_in = obj.importTXT(obj.fileImp,'\t',1:8,1);                            % import Leica cst file
            TS_obs(:,[1 2 3 5 7 9 10 13]) = cst_in;                                 % reassign the file to the variable TS_obs in the right order
            TS_obs(:,[3 5 7 9 10]) = cellfun(@str2num,...
                TS_obs(:,[3 5 7 9 10]),'UniformOutput',false);                      % convert format of column that are numbers (string to numbers)
            TS_obs(:,[4 6 8]) = num2cell(~cellfun(@isempty,TS_obs(:,[5 7 9])));     % set active all the measurement done
            TS_obs(:,11) = num2cell( cell2mat(TS_obs(:,7)) > 200);                  % find left circle measurement (the ones that have zenith grather than 200)
            TS_obs(:,12) = num2cell(~cellfun(@isempty,strfind(TS_obs(:,13),'NP'))); % find no prism measuremente by identifying the character NP in the point code
            
            TS_name_list = unique(TS_obs(:,1));                                     % find list of station names
            
            % loop to rename all the stations with numeric values
            PT_ren = nan(size(TS_obs,1),2);                       % initialize the variable where all the rows with renamed station are saved
            for i = 1 : size(TS_name_list,1)
                TS_pos =  strcmp(TS_obs(:,1),TS_name_list(i));    % extract the rows measured by the station at position i
                PT_ren(TS_pos,1) = i .* ones(sum(TS_pos,1),1);   % rename the station with the iteration number
                PT_ren(TS_pos,2) = cell2mat(TS_obs(TS_pos,3));    % save the station heigth near the
            end
            
            [TS_list, idx_list, idx_obs] = unique(PT_ren,'rows');                   % find unique value between station and their indexes
            TS_list = num2cell(TS_list);
            TS_obs(:,14) = num2cell(idx_obs);                                       % associate to each row the progressive number of the station
            TS_list(:,1) = TS_obs(idx_list,1);                                      % change name of stations from numbers to strings
        end % end of importLeicaTS
        
        % import mm2 topcon file
        function [TS_obs, TS_list] = importMM2(obj)
            %    [pt st op] = impoMM2 (filename)
            %        - filename --> stringa contenente il nome del file ( con il
            %        percorso)
            %
            %        --> pt sara' una cell contente nell'ordine
            %        - Nome Stazione
            %        - Nome Punto
            %        - Altezza strumentale
            %        - Azimut
            %        - Zenit
            %        - Distanza inclinata
            %        - Altezza prisma
            %        - Codice punto
            %        - Cerchio sinistro?
            %        --> st sara' una cell contenente nell'ordine
            %        - Nome stazione
            %        - Altezza strumentale
            %        - Sigma hs
            %        - Pressione
            %        - Codice
            
            fid = fopen(obj.fileImp,'r');                                          % open file in read only mode
            ReadData = textscan(fid,'%s%s%s%s%s%s%s%s%s%s','delimiter','');    % import data from file
            fclose(fid);                                                        % close the file
            ReadData = strtrim(ReadData);                                       % remove the spaces at the end of each imported data
            ImportedData = cell(size(ReadData{1,1},1),size(ReadData,2));        % initializing the variable where imported data are stored
            
            % rearrange imported data in a cell array with each row of the file on one
            % row
            for i = 1:size(ReadData,2)
                ImportedData(:,i) = ReadData{1,i};
            end
            
            idx_st = [find(strcmp('S|',ImportedData(:,1)) == 1);               % search for the identifier of the station and save the row where it is
                size(ImportedData,1) + 1];                               % (last row is the total number of row + 1, like it is the line of the new station)
            n_obs = sum(strcmp('P|',ImportedData(:,1)));                       % count the total number of observations
            idx_obs = [0;
                cumsum(idx_st(2:end) - (idx_st(1:end-1) + 1))];               % find row where each station finish in the final output variable (so row + 1 is the row where the next station start)
            TS_obs = cell(n_obs,8);                                            % allocating space for the imported data
            
            % loop to assign the imported values to the output variable of the
            % observations
            for i = 1 : length(idx_obs) - 1
                interval_obs = idx_obs(i) + 1 : idx_obs(i + 1);                     % interval used for current station observations in final output variable
                interval_st = idx_st(i) + 1 : idx_st(i + 1) - 1;                    % interval used for current station observations in imported data
                TS_obs(interval_obs,1) = {ImportedData{idx_st(i),2}};   %#ok<CCAT1> % Station name
                TS_obs(interval_obs,2) = ImportedData(interval_st,2);               % Point name
                TS_obs(interval_obs,3) = {ImportedData{idx_st(i),4}};   %#ok<CCAT1> % Instrumental heigth
                TS_obs(interval_obs,5) = ImportedData(interval_st,5);               % Azimuth
                TS_obs(interval_obs,7) = ImportedData(interval_st,6);               % Zenith
                TS_obs(interval_obs,9) = ImportedData(interval_st,7);               % Distance
                TS_obs(interval_obs,10) = ImportedData(interval_st,4);              % Prism height
                TS_obs(interval_obs,13) = ImportedData(interval_st,3);              % Point code
                TS_obs(interval_obs,14) = num2cell(i .* ones(size(interval_obs)));  % Station number
            end
            
            TS_obs(:,[3 5 7 9 10]) = cellfun(@str2num,...
                TS_obs(:,[3 5 7 9 10]),'UniformOutput',false);                      % convert format of column that are numbers (string to numbers)
            TS_obs(:,[4 6 8]) = num2cell(~cellfun(@isempty,TS_obs(:,[5 7 9])));     % set active all the measurement done
            TS_obs(:,11) = num2cell( cell2mat(TS_obs(:,7)) > 200);                  % find left circle measurement (the ones that have zenith grather than 200)
            TS_obs(:,12) = num2cell(~cellfun(@isempty,strfind(TS_obs(:,13),'NP'))); % find no prism measuremente by identifying the character NP in the point code
            
            idx_st(end) = [];                                                       % remove last row of idx_st (that was added for computational reasons)
            TS_list = cell(length(idx_st),2);                                       % allocate space for tha variable that contain the list of stations
            TS_list(:,1) = ImportedData(idx_st,2);                                  % save stations name
            TS_list(:,2) = num2cell(str2double(ImportedData(idx_st,4)));            % save instrumental height
        end % end of importMM2
        
        % function to import the levelling observation from txt file
        function Lev_obs = importLEVtxt(obj,sigma)
            txt_in = obj.importTXT(obj.fileImp,obj.txt_del_list{obj.txt_del}...
                ,obj.txt_order,obj.txt_header);                                     % import txt file based on the option set
            Lev_obs(:,[2 3 4]) = txt_in(:,[1 2 3]);                                 % import the column of Points name and Delta heigth
            idx = cellfun(@isempty,Lev_obs(:,2));                                   % find empty measuremnt
            Lev_obs(:,1) = num2cell(~idx);                                          % set flag to not use in empty measurement
            Lev_obs(:,5) = cell(size(Lev_obs,1),1);                                 % initialize last column of the imported variable
            D = str2double(txt_in(:,4));                                            % imported distance
            D(isnan(D),:) = 0;                                                      % set to zero the empty distance
            % find empty observations
            if ~isempty(Lev_obs(~idx,2))              % do the opertion only if there are some zenith
                Sapr = sigma{1,1} + sigma{1,2} .* 10^-6 .* D(~idx,:);        % compute the apriori known sigma
                Lev_obs(~idx,5) = num2cell(Sapr);                            % save the apriori sigma (conversion from cell to matrix is needed)
            end
        end
        
        % import GPS txt file
        function GPS_obs = importGPStxt(obj)
            txt_in = obj.importTXT(obj.fileImp,obj.txt_del_list{obj.txt_del}...
                ,obj.txt_order,obj.txt_header);                                     % import txt file based on the option set
            GPS_obs(:,[2 3 4 5 6]) = txt_in(:,1:5);                                 % import column of point name, code and X, Y, Z
            GPS_obs(:,1) = {true};                                                  % set all the point active (to be used)
            GPS_obs(:,[3 4 5]) = cellfun(@str2num,GPS_obs(:,[3 4 5]),'UniformOutput',false);  % convert the data from string to number
            M0 = str2double(txt_in(:,6));                                           % import M0 parameter
            M0(isnan(M0),1) = 1;                                                    % find non numerical value of M0 and set them to 1
            Q = str2double(txt_in(:,7:12));                                         % import cofactor matrix values
            Q(isnan(Q)) = 0;                                                        % set the not a number values to 0
            C = repmat(M0.^(2),1,size(Q,2)) .* Q;                                   % compute covariance matrix
            idx = zeros(size(C));                                                   % initialize matrix index
            idx(:,[1 4 6]) = C(:,[1 4 6])==0;                                       % find elements on first diagonal that are zero
            C(logical(idx)) = 10 ^ - 8;                                             % set the zeros values of the diagonal of covariance matrix to 10^-8
            GPS_obs(:,7:12) = num2cell(C);                                          % store the covariance matrix in the final variable
        end
        
        % import GPS Leica cst file
        function GPS_obs = importGPScst(obj)
            txt_in = obj.importTXT(obj.fileImp,'\t',1:12,1);                        % import txt file based on the option set
            GPS_obs(:,[2 3 4 5 6]) = txt_in(:,1:5);                                 % import column of point name, code and X, Y, Z
            GPS_obs(:,1) = {true};                                                  % set all the point active (to be used)
            GPS_obs(:,[3 4 5]) = cellfun(@str2num,GPS_obs(:,[3 4 5]),'UniformOutput',false);  % convert the data from string to number
            M0 = str2double(txt_in(:,6));                                           % import M0 parameter
            M0(isnan(M0),1) = 1;                                                    % find non numerical value of M0 and set them to 1
            Q = str2double(txt_in(:,7:12));                                         % import cofactor matrix values
            Q(isnan(Q)) = 0;                                                        % set the not a number values to 0
            C = repmat(M0.^(2),1,size(Q,2)) .* Q;                                   % compute covariance matrix
            idx = zeros(size(C));                                                   % initialize matrix index
            idx(:,[1 4 6]) = C(:,[1 4 6])==0;                                       % find elements on first diagonal that are zero
            C(logical(idx)) = 10 ^ - 8;                                             % set the zeros values of the diagonal of covariance matrix to 10^-8
            GPS_obs(:,7:12) = num2cell(C);                                          % store the covariance matrix in the final variable
        end
        
        % import BL txt file
        function BL_obs = importBLtxt(obj)
            txt_in = obj.importTXT(obj.fileImp,obj.txt_del_list{obj.txt_del}...
                ,obj.txt_order,obj.txt_header);                                     % import txt file based on the option set
            BL_obs(:,[2 3 4 5 6 7]) = txt_in(:,1:6);                                % import column of point name, code and X, Y, Z
            BL_obs(:,1) = {true};                                                   % set all the point active (to be used)
            BL_obs(:,[4 5 6]) = cellfun(@str2num,BL_obs(:,[4 5 6]),'UniformOutput',false);  % convert the data from string to number
            M0 = str2double(txt_in(:,7));                                           % import M0 parameter
            M0(isnan(M0),1) = 1;                                                    % find non numerical value of M0 and set them to 1
            Q = str2double(txt_in(:,8:13));                                         % import cofactor matrix values
            Q(isnan(Q)) = 0;                                                        % set the not a number values to 0
            C = repmat(M0.^(2),1,size(Q,2)) .* Q;                                   % compute covariance matrix
            idx = zeros(size(C));                                                   % initialize matrix index
            idx(:,[1 4 6]) = C(:,[1 4 6])==0;                                       % find elements on first diagonal that are zero
            C(logical(idx)) = 10 ^ - 8;                                             % set the zeros values of the diagonal of covariance matrix to 10^-8
            BL_obs(:,8:13) = num2cell(C);                                           % store the covariance matrix in the final variable
        end
        
        % import BL Leica cst file
        function BL_obs = importBLcst(obj)
            txt_in = obj.importTXT(obj.fileImp,'\t',1:13,1);                        % import txt file based on the option set
            BL_obs(:,[2 3 4 5 6 7]) = txt_in(:,1:6);                                % import column of point name, code and X, Y, Z
            BL_obs(:,1) = {true};                                                   % set all the point active (to be used)
            BL_obs(:,[4 5 6]) = cellfun(@str2num,BL_obs(:,[4 5 6]),'UniformOutput',false);  % convert the data from string to number
            M0 = str2double(txt_in(:,7));                                           % import M0 parameter
            M0(isnan(M0),1) = 1;                                                    % find non numerical value of M0 and set them to 1
            Q = str2double(txt_in(:,8:13));                                         % import cofactor matrix values
            Q(isnan(Q)) = 0;                                                        % set the not a number values to 0
            C = repmat(M0.^(2),1,size(Q,2)) .* Q;                                   % compute covariance matrix
            idx = zeros(size(C));                                                   % initialize matrix index
            idx(:,[1 4 6]) = C(:,[1 4 6])==0;                                       % find elements on first diagonal that are zero
            C(logical(idx)) = 10 ^ - 8;                                             % set the zeros values of the diagonal of covariance matrix to 10^-8
            BL_obs(:,8:13) = num2cell(C);                                           % store the covariance matrix in the final variable
        end
        
        % lock import figure
        function lockImport(obj)
            h = fieldnames(obj.ih);
            obj.prevIstatus = cell(size(h));
            for i = 1:size(h,1)
                prop = get(obj.ih.(h{i}));
                if sum(strcmp('Enable',fieldnames(prop)))~=0
                    obj.prevIstatus{i,1} = get(obj.ih.(h{i}),'Enable');
                    set(obj.ih.(h{i}),'Enable','off');
                else
                    obj.prevIstatus{i,1} = 'noEnable';
                end
            end
        end
        
        % unlock import figure
        function unlockImport(obj)
            h = fieldnames(obj.ih);
            for i = 1:size(h,1)
                if ~strcmp(obj.prevIstatus{i},'noEnable')
                    set(obj.ih.(h{i}),'Enable',obj.prevIstatus{i});
                end
            end
        end
    end
    
    % Export GUI functions %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    methods
        function coord2xls(obj)
            [file, path] = uiputfile(strcat(obj.GUI_lastPath,'*.xlsx'),'Export adjusted coordinates...');
            if isempty(file) || sum(file == 0) || sum(path == 0) % check if a file was chosen or not (if not the function was returned)
                msgbox('Error in file name','Error');
                return
            else fname = strcat(path,file); % create the filename with the full path
            end
            % cartesian
            cartT = {'Point', 'X (m)', 'Y (m)', 'Z (m)', 'Point Code', 'Cxx', 'Cxy',...
                'Cxz', 'Cyy', 'Cyz', 'Czz', 'Ext. reliability X (m)', 'Ext. reliability Y (m)','Ext. reliability Z (m)'};
            cart = [cartT; obj.coord(:,1) obj.coord(:,2:4) obj.coord(:,8) obj.coord(:,12:17) obj.coord(:,22:24)];
            % geodetic
            geoT = {'Point', 'fi (deg)', 'lam (deg)', 'h (deg)', 'Point Code', 'std fi (mm)', 'std lam (mm)',...
                'std h (mm)', 'Ext. reliability fi (m)', 'Ext. reliability lam (m)','Ext. reliability h (m)'};
            geo  = [geoT; obj.coord(:,1) obj.coord(:,5:7) obj.coord(:,8) obj.coord(:,9:11) obj.coord(:,19:21)];
            % write excel
            xlswrite(fname,cart,'Cartesian');
            xlswrite(fname,geo,'Geodetic');
            removeEmptySheet(file,path);
        end
    end
    
    % constraints panel function %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    methods
        % open constraints GUI
        function openConstraintsUI(obj,handles)
            obj.ch = handles;
            set(obj.ch.pCoord,'SelectedObject',obj.ch.rGeodetic);
            obj.setTableConstraints;
        end
        
        % function to visualize constraints in table
        function setTableConstraints(obj)
            switch get(obj.ch.pCoord,'SelectedObject') % check coordinates type
                case obj.ch.rGeodetic % geodetic
                    set(obj.ch.tConstraints,'Data',[]);
                    drawnow
                    pause(0.001)
                    c_desc = {'Use','Point','Longitude','Latitude', 'Ellip. heigth', 'Point code'};   % column name
                    c_width = {30,100, 100, 100, 100, 100};                                        % column width
                    c_format = {'logical','char', 'numeric', 'numeric','short', 'char'};                        % column format
                    c_editable = [true, false, false, false, false, false];                           % column editable
                    % set parameter in table
                    set(obj.ch.tConstraints,'ColumnName',c_desc,...
                        'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable,'RowName', {'numbered'});
                    if isempty(obj.constraints) == 0 % check f there are observations of the requested type
                        Data = obj.constraints(:,[1 2 6 7 8 9]);  % extract the column with needed data
                        Data(:,[3 4]) = cellfun(@obj.dec2sex,Data(:,[3 4]),'UniformOutput',0);   % convert angles in sexagesimal view
                        Data(:,5) = cellfun(@(x) sprintf('%.3f',x),Data(:,5),'UniformOutput',0); % convert heigth in 3 decimal position numbers
                        set(obj.ch.tConstraints,'Data',Data);
                    else set(obj.ch.tConstraints,'Data',[]);
                    end
                case obj.ch.rCartesian % cartesian
                    set(obj.ch.tConstraints,'Data',[]);
                    drawnow
                    pause(0.001)
                    c_desc = {'Use','Point','X (m)','Y (m)', 'Z (m)', 'Point code'};                  % column name
                    c_width = {30, 100, 100, 100, 100, 100};                                        % column width
                    c_format = {'logical', 'char', 'short', 'short','short', 'char'};                      % column format
                    c_editable = [true, false, false, false, false, false];                           % column editable
                    % set parameter in table
                    set(obj.ch.tConstraints,'ColumnName',c_desc,...
                        'ColumnFormat',c_format,'ColumnWidth',c_width,'ColumnEditable',c_editable,'RowName', {'numbered'});
                    if isempty(obj.constraints) == 0 % check f there are observations of the requested type
                        Data = obj.constraints(:,[1 2 3 4 5 8]); % extract the column with the needed data
                        Data(:,3:5) = cellfun(@(x) strcat(sprintf('%.3f',x)),Data(:,3:5),'UniformOutput',false);
                        set(obj.ch.tConstraints,'Data',Data);
                    else set(obj.ch.tConstraints,'Data',[]);
                    end
            end
        end
               
         % import coordinates of contraints
        function importConstraints(obj)
            % get the order of the fields
            txt_ord = nan(obj.n_field,1);
            for i = 1 : obj.n_field
                h = eval(['obj.ih.field' num2str(i)]);
                txt_ord(i) = get(h,'Value');
            end
            if length(unique(txt_ord)) == obj.n_field % check if fields are repeted only once
                obj.txt_order = txt_ord;
                if  ~isnan(str2double(get(obj.ih.eHeader,'String'))) % check if delimiter is a number
                    % get the number of headerlines
                    obj.txt_header = str2double(get(obj.ih.eHeader,'String'));
                    % get the chosen delimiter
                    obj.txt_del = get(obj.ih.pDelimiter,'Value');
                    % import coordinates from txt file
                    txt_in = obj.importTXT(obj.fileImp,obj.txt_del_list{obj.txt_del}...
                        ,obj.txt_order,obj.txt_header);
                    % allocate space for coordinates variable
                    coord_in = cell(size(txt_in,1),8);
                    % assign name and point code to the final variable
                    coord_in(:,[1 8]) = txt_in(:,[1 5]);
                    % get ellipsoid parameters
                    ellips = getfield(obj.ellipsoid,obj.SelectedEllips); %#ok<GFLD>
                    a = ellips.a;       % majoer semiaxis
                    f = ellips.f;   % flattening
                    % check wich type of coordinates is selected
                    switch get(obj.ih.pCoordType,'SelectedObject');
                        case obj.ih.rCartesian % Cartesian gecentric coordinates
                            coord_in(:,[2 3 4]) = num2cell(cellfun(@str2num,txt_in(:,[2 3 4])));     % store imported values
                            XYZ_mat = cell2mat(coord_in(:,[2 3 4]));                       % convert XYZ into a matrix
                            flh_mat = obj.cart2geo(XYZ_mat, a, f);                         % extract geodetic coordinates in radians
                            flh_mat(:,1:2) = flh_mat(:,1:2) .* 180 ./ pi;                  % convert angle to sexadecimal degrees
                            coord_in(:,[5 6 7]) = num2cell(flh_mat);                       % store geodetic coordinates
                            obj.GUI_coord = 2;                                             % set the kind of coordinates to visulize (cartesian)
                        case obj.ih.rGeodSex % Sexagesimal geodetic coordinates
                            coord_in(:,[5 6]) = num2cell(cellfun(@obj.sex2dec,txt_in(:,[2 3])));     % store imported values (angles), converting them into sexadecimal
                            coord_in(:,7) = num2cell(cellfun(@str2num,txt_in(:,4)));                 % store imported heigth values
                            flh_mat = cell2mat(coord_in(:,[5 6 7]));                       % convert flh into a matrix
                            flh_mat(:,1:2) = flh_mat(:,1:2) .* pi ./ 180;                  % convert angle to radians
                            XYZ_mat = obj.geo2cart(flh_mat, a, f);                         % extract geodetic coordinates in radians
                            coord_in(:,[2 3 4]) = num2cell(XYZ_mat);                       % store cartesian geocentric coordinates
                            obj.GUI_coord = 1;                                             % set the kind of coordinates to visulize (geodetic)
                        case obj.ih.rGeodDec % Sexadecimal geodetic coordinates
                            coord_in(:,[5 6 7]) = num2cell(cellfun(@str2num,txt_in(:,[2 3 4])));     % store imported values
                            flh_mat = cell2mat(coord_in(:,[5 6 7]));                       % convert flh into a matrix
                            flh_mat(:,1:2) = flh_mat(:,1:2) .* pi ./ 180;                  % convert angle to radians
                            XYZ_mat = obj.geo2cart(flh_mat, a, f);                         % extract geodetic coordinates in radians
                            coord_in(:,[2 3 4]) = num2cell(XYZ_mat);                       % store cartesian geocentric coordinates
                            obj.GUI_coord = 1;                                             % set the kind of coordinates to visulize (geodetic)
                    end
                    coord_in = [num2cell(true(size(coord_in,1),1)), coord_in];
                    % store data in variables
                    % check if existing data must or not be replaced
                    if get(obj.ih.rAdd,'Value') == 1  % Adding to existing data is required
                        obj.constraints = [obj.constraints; coord_in];                      % store variable by adding them to existing one
                    elseif get(obj.ih.rOver,'Value') == 1 % Overwriting existing data
                        obj.constraints = coord_in;
                    end
                    % close the figure
                    switch obj.CoordImpType
                        case 1
                            close(obj.ih.fImportCoord);
                        case 2
                            close(obj.ih.fImportCoord);
                    end
                    obj.removeAdjustment;
                    obj.Saved = 0;                                              % set saved to 0
                    obj.setTableConstraints
                else msgbox('Set numerical value in number of headerlines','Error','error') % error if there is no numerica numb of headerlines
                end % end of second if
            else msgbox('A field was chosen double','Error','error') % error if there is a field repeated more than once
            end
        end
        
        % revome all the constraints
        function removeAllConstraints(obj)
            choice = questdlg('Are you really shure to remove all the constraints?','Remove all constraints','Yes','No','No');
            switch choice
                case 'Yes'
                    obj.constraints = cell(0,size(obj.constraints,2));
                    obj.removeAdjustment;
                    obj.Saved = 0;
                    obj.setTableConstraints
            end
        end
        
                % lock import figure
        function lockConstraints(obj)
            h = fieldnames(obj.ch);
            obj.prevCstatus = cell(size(h));
            for i = 1:size(h,1)
                prop = get(obj.ch.(h{i}));
                if sum(strcmp('Enable',fieldnames(prop)))~=0
                    obj.prevCstatus{i,1} = get(obj.ch.(h{i}),'Enable');
                    set(obj.ch.(h{i}),'Enable','off');
                else
                    obj.prevCstatus{i,1} = 'noEnable';
                end
            end
        end
        
        % unlock import figure
        function unlockConstraints(obj)
            h = fieldnames(obj.ch);
            for i = 1:size(h,1)
                if ~strcmp(obj.prevCstatus{i},'noEnable')
                    set(obj.ch.(h{i}),'Enable',obj.prevCstatus{i});
                end
            end
        end
    end
    
    % setting option function %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    methods
        % open figure to set geoid and ellipsoid
        function OpenSetRFOption(obj,handles)
            obj.oh = handles;
            set(obj.oh.tEllipsoid,'String','Ellipsoid:');
            set(obj.oh.popEllipsoid,'String',fieldnames(obj.ellipsoid),'Value',find(strcmp(fieldnames(obj.ellipsoid),obj.SelectedEllips),1,'first'));
            set(obj.oh.tGeoid,'String','Geoid:');
            set(obj.oh.popGeoid,'String',fieldnames(obj.geoid),'Value',find(strcmp(fieldnames(obj.geoid),obj.SelectedGeoid),1,'first'));
            set(obj.oh.fRFsetting, 'Name','Reference frame option');
        end
        
        % close the figure and store the preferences
        function setRFoption(obj)
            SG = get(obj.oh.popGeoid,'Value');
            G = fieldnames(obj.geoid);
            SE = get(obj.oh.popEllipsoid,'Value');
            E = fieldnames(obj.ellipsoid);
            if strcmp(G{SG},obj.SelectedGeoid) && strcmp(E{SE},obj.SelectedEllips)
                close(obj.oh.fRFsetting)
            else
                obj.SelectedGeoid = G{SG};
                obj.SelectedEllips = E{SE};
                obj.removeAdjustment;
            end
        end
    end
    
    % Adjustment setting panel %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    methods
        function openAdjOpt(obj, handles)
            obj.oh = handles;
            set(obj.oh.fAdjOpt,'Name','Adjustment options');
            set(obj.oh.eImax,'String',obj.LSoptions.imax);
            set(obj.oh.eGPSw,'String',obj.LSoptions.GPSw);
            set(obj.oh.eAlfa,'String',obj.LSoptions.alfa*100);
            set(obj.oh.eBeta,'String',obj.LSoptions.beta*100);
            set(obj.oh.cBessel,'Value',obj.LSoptions.bessel);
            set(obj.oh.cAtmo,'Value',obj.LSoptions.atmo);
            set(obj.oh.cBessel,'Enable','off');
            set(obj.oh.cAtmo,'Enable','off');
        end
        
        function getAdjOpt(obj)
            obj.LSoptions.imax = str2double(get(obj.oh.eImax,'String'));
            obj.LSoptions.GPSw = str2double(get(obj.oh.eGPSw,'String'));
            obj.LSoptions.alfa = str2double(get(obj.oh.eAlfa,'String'))/100;
            obj.LSoptions.beta = str2double(get(obj.oh.eBeta,'String'))/100;
            obj.LSoptions.bessel = get(obj.oh.cBessel,'Value');
            obj.LSoptions.atmo = get(obj.oh.cAtmo,'Value');
        end
    end
    
    % Computation function %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    methods
        % function to tranform all the point ID into a number from 1 to max
        % number of point
        function NameTranslation(obj)
            % create list of the point of total station observations if
            % there are observation
            if not(isempty(obj.TS_obs))
                used_a = cell2mat(obj.TS_obs(:,4));
                used_z = cell2mat(obj.TS_obs(:,6));
                used_d = cell2mat(obj.TS_obs(:,8));
                A_pt = [obj.TS_obs(used_a,1); obj.TS_obs(used_a,2)];
                Z_pt = [obj.TS_obs(used_z,1); obj.TS_obs(used_z,2)];
                D_pt = [obj.TS_obs(used_d,1); obj.TS_obs(used_d,2)];
                TS_pt = unique([A_pt; Z_pt; D_pt]);
            else TS_pt = cell(0);
            end
            % create list of the point of levelling observations if there
            % are observations
            if not(isempty(obj.lev_obs))
                used_lev = obj.lev_obs(:,1);                 % extract boolean to understand used baseline
                lev_pt = [unique(obj.lev_obs(used_lev,2));
                    unique(obj.lev_obs(used_lev,3))];
            else lev_pt = cell(0);
            end
            % create list of the point of GPS single point observations if
            % there are observations
            if not(isempty(obj.sp_obs))
                used_sp = cell2mat(obj.sp_obs(:,1));                 % extract boolean to understand used baseline
                sp_pt = unique(obj.sp_obs(used_sp,2));
            else sp_pt = cell(0);
            end
            % create list of the point of GPS Baseline observations if
            % there are observations
            if not(isempty(obj.bl_obs))
                used_bl = cell2mat(obj.bl_obs(:,1));                 % extract boolean to understand used baseline
                bl_pt = [unique(obj.bl_obs(used_bl,2));
                    unique(obj.bl_obs(used_bl,3))];
            else bl_pt = cell(0);
            end
            % create a list of points in constraints table
            if not(isempty(obj.constraints))
                used_const = cell2mat(obj.constraints(:,1)); 
                const_pt = unique(obj.constraints(used_const,2));
            else const_pt = cell(0);
            end
            
            % create an unique list for all the kind of observations
            pto_list = [TS_pt; lev_pt; sp_pt; bl_pt; const_pt];   % create the whole list
            pto_list = unique(pto_list);                % remove duplicated names
            
            % check if there are some coordinates (the variable mustn't be empty)
            if ~isempty(obj.coord)
                ptc_list = unique(obj.coord(:,1));          % extract the list of coordinates
                % check if there are some duplicated point (only if the original and the final variable are different)
                if size(ptc_list,1) ~= size(obj.coord,1)
                    msgbox('There are some duplicated point in approximated coordinates','Error','error') % error message, duplicated point in coordinates
                    return % interrupt the function if there is the error
                end
                ptc_list = sort(ptc_list);         % extract a sorted list of point for the coordinates point
                pto_list = sort(pto_list);         % extract a sorted list of point for the observations point
                
                % check if some point are missing (the length of the two unique
                % vector is different)
                if size(pto_list,1) ~= size(ptc_list,1)
                    switch size(pto_list,1) > size(ptc_list,1) % check if observ points are more than coord points
                        case true
                            % A variable where zero means is present point isn't
                            % present in coord list and one it is present is
                            % used to understan wich point are missing
                            % check for each obs point if it is present in coordinates
                            % list
                            iscoord = ismember(pto_list,ptc_list);
                            isntcoord = pto_list(~iscoord,1); % extract list of point that aren't present in the coordinates list
                            % create a string for the list of point separated with comma
                            isntcoord_txt = isntcoord{1};
                            for i = 2:size(isntcoord,1)
                                isntcoord_txt = [isntcoord_txt ', ' isntcoord{i}]; %#ok<*AGROW>
                            end
                            % error message
                            msgbox(['Point ' isntcoord_txt ' missed in approximated coordinates'],'Error','error');
                            return
                        case false
                            % Find the value of coordinates that are not
                            % present into observations
                            isobs = ismember(ptc_list,pto_list);
                            isntobs = ptc_list(~isobs,1); % extract list of point that aren't present in the coordinates list
                            % create a string for the list of point separated with comma
                            isntobs_txt = isntobs{1};
                            for i = 2:size(isntobs,1)
                                isntobs_txt = [isntobs_txt ', ' isntobs{i}]; %#ok<*AGROW>
                            end
                            % remove the point from the coordinates list
                            for i = 1:size(isntobs,1)
                                idx_coord = strcmp(isntobs{i,1},obj.coord(:,1)); % identificate the index in the coordinates variable
                                obj.coord(idx_coord,18) = {0};                   % save not calculated into line
                                idx_list = strcmp(isntobs{i,1},ptc_list(:,1));   % identificate the index in the coordinates name list
                                ptc_list(idx_list,:) = [];                       % remove the not used line from the coordinates list
                            end
                            % now check if all the point are present
                            if size(pto_list,1) > size(ptc_list,1)
                                % A variable where zero mean is present point isn't
                                % present in coord list and one it is present is
                                % used to understan wich point are missing
                                % check for each obs point if it is present in coordinates
                                % list
                                iscoord = ismember(pto_list,ptc_list);
                                isntcoord = pto_list(~iscoord,1); % extract list of point that aren't present in the coordinates list
                                % create a string for the list of point separated with comma
                                isntcoord_txt = isntcoord{1};
                                for i = 2:size(isntcoord,1)
                                    isntcoord_txt = [isntcoord_txt ', ' isntcoord{i}]; %#ok<*AGROW>
                                end
                                % error message (missed point and not used
                                % point
                                msgbox(['Point ' isntcoord_txt ' missed in approximated coordinates and point'...
                                    isntobs_txt ' present in approximated coordinate will not used during adjustment'...
                                    ],'Error','error');
                                return
                            else % if the two vector have the same length display message of not used point
                                % error message
                                msgbox(['Point ' isntobs_txt ' present in approximated coordinate will not used during adjustment'],'Error','error');
                            end
                    end
                end
            else ptc_list = pto_list;
            end
            pto_list = sort(pto_list);
            ptc_list = sort(ptc_list);
            % check if some points have the wrong name
            if sum(~strcmp(pto_list,ptc_list))~= 0
                iscoord = zeros(size(pto_list,1),1); % initialize a variable where zero mean is present point isn't present in coord list and one it is present
                % check for each obs point if it is present in coordinates
                % list
                for i = 1 : size(pto_list,1)
                    iscoord(i,1) = sum(strcmp(pto_list{i,1},ptc_list));
                end
                isntcoord = pto_list(iscoord == 0,1); % extract list of point that aren't present in the coordinates list
                % create a string for the list of point separated with comma
                isntcoord_txt = isntcoord{1};
                for i = 2:size(isntcoord,1)
                    isntcoord_txt = [isntcoord_txt ', ' isntcoord{i}]; %#ok<*AGROW>
                end
                % error message
                msgbox(['Point ' isntcoord_txt ' missed in approximated coordinates|Probably name is written wrong'],'Error','error');
                return
            end
            % prepare the variable needed during calculation
            if ~isempty(obj.TS_obs)
                % extract the used azimuth. Variable is composed in the
                % following way:
                %|St.|Pt.|Azimut|hs|hp|Sigma|Left circ|St.numb|
                A_obs_ren = obj.TS_obs(used_a,[1 2 5 3 10 15 11 14]);
                % extract the used zenith. Variable is composed in the
                % following way:
                %|St.|Pt.|Zenit|hs|hp|Sigma|Left circ|St.numb|
                Z_obs_ren = obj.TS_obs(used_z,[1 2 7 3 10 16 11 14]);
                % extract the used distance. Variable is composed in the
                % following way:
                %|St.|Pt.|Distance|hs|hp|Sigma|St.numb|
                D_obs_ren = obj.TS_obs(used_d,[1 2 9 3 10 17 14]);
                % extract the station list (whithout the sigma apriori for
                % each station)
                % St.|hs|sigma hs|Beta|
                TS_list_ren = obj.TS_list(:,1:4);
            else % initialize variables empty
                A_obs_ren = ones(0,8);
                Z_obs_ren = ones(0,8);
                D_obs_ren = ones(0,7);
                TS_list_ren = ones(0,4);
            end
            if ~isempty(obj.lev_obs)
                % extract the used levelling obs. Variable is composed in the
                % following way:
                %|Back.|For.|Heigth diff.|Sigma|
                lev_obs_ren = obj.lev_obs(used_lev,[2 3 4 5]);
            else % initialize variable empty
                lev_obs_ren = ones(0,4);
            end
            if ~isempty(obj.sp_obs)
                % extract the used levelling obs. Variable is composed in the
                % following way:
                %|Pt.|X|Y|Z|Cxx|Cxy|Cxz|Cyy|Cyz|Czz|
                sp_obs_ren = obj.sp_obs(used_sp,[2 3 4 5 7 8 9 10 11 12]);
            else % initialize variable empty
                sp_obs_ren = ones(0,10);
            end
            if ~isempty(obj.bl_obs)
                % extract the used levelling obs. Variable is composed in the
                % following way:
                %|St.|Pt.|X|Y|Z|Cxx|Cxy|Cxz|Cyy|Cyz|Czz|
                bl_obs_ren = obj.bl_obs(used_bl,[2 3 4 5 6 8 9 10 11 12 13]);
            else % initialize variable empty
                bl_obs_ren = ones(0,11);
            end
            if ~isempty(obj.constraints)
                %|Pt.|X|Y|Z|
                constXYZ_ren = obj.constraints(used_const,[2,3,4,5]);
                %|Pt.|fi|la|h|
                constflh_ren = obj.constraints(used_const,[2,6,7,8]);
            else
                constXYZ_ren = ones(0,4);
                constflh_ren = ones(0,4);
            end
            if ~isempty(obj.coord)
                nocoord = false;
                used_coord = logical(cell2mat(obj.coord(:,18))); % extract the used coordinates during computataion (column 18 is the one that identify if it is used)
                % extract list of approximated coordinates in the following way
                % |Pt.|fi|la|h|
                coord_ren = obj.coord(used_coord,[1 5 6 7]);
            else
                nocoord = true;
                coord_ren = cell(size(ptc_list,1),4);
                coord_ren(:,1) = ptc_list;
                obj.coord = cell(size(ptc_list,1),18);
                obj.coord(:,1) = ptc_list;
                obj.coord(:,18) = num2cell(true(size(ptc_list,1),1));
            end
            % loop used to rename all the point using the list created from
            % coordinates
            for i = 1:size(coord_ren,1)
                % Rename azimuth
                if not(isempty(A_obs_ren))
                    % extract index of point that matches at current
                    % iteration
                    idx_A_s = strcmp(A_obs_ren(:,1),coord_ren(i,1));
                    idx_A_p = strcmp(A_obs_ren(:,2),coord_ren(i,1));
                    % Assign the number of row point list to the selected
                    % rows
                    A_obs_ren(idx_A_s,1) = {i};
                    A_obs_ren(idx_A_p,2) = {i};
                end
                % Rename zenith
                if not(isempty(Z_obs_ren))
                    % extract index of point that matches at current
                    % iteration
                    idx_Z_s = strcmp(Z_obs_ren(:,1),coord_ren(i,1));
                    idx_Z_p = strcmp(Z_obs_ren(:,2),coord_ren(i,1));
                    % Assign the number of row point list to the selected
                    % rows
                    Z_obs_ren(idx_Z_s,1) = {i};
                    Z_obs_ren(idx_Z_p,2) = {i};
                end
                % Rename
                if not(isempty(D_obs_ren))
                    % extract index of point that matches at current
                    % iteration
                    idx_D_s = strcmp(D_obs_ren(:,1),coord_ren(i,1));
                    idx_D_p = strcmp(D_obs_ren(:,2),coord_ren(i,1));
                    % Assign the number of row point list to the selected
                    % rows
                    D_obs_ren(idx_D_s,1) = {i};
                    D_obs_ren(idx_D_p,2) = {i};
                end
                if not(isempty(TS_list_ren))
                    % extract index of point that matches at current
                    % iteration
                    idx_TS_l = strcmp(TS_list_ren(:,1),coord_ren(i,1));
                    % extract index of point that matches at current
                    % iteration
                    TS_list_ren(idx_TS_l,1) = {i};
                end
                if not(isempty(lev_obs_ren))
                    % extract index of point that matches at current
                    % iteration
                    idx_lev_b = strcmp(lev_obs_ren(:,1),coord_ren(i,1));
                    idx_lev_f = strcmp(lev_obs_ren(:,2),coord_ren(i,1));
                    % extract index of point that matches at current
                    % iteration
                    lev_obs_ren(idx_lev_b,1) = {i};
                    lev_obs_ren(idx_lev_f,2) = {i};
                end
                if not(isempty(sp_obs_ren))
                    % extract index of point that matches at current
                    % iteration
                    idx_sp = strcmp(sp_obs_ren(:,1),coord_ren(i,1));
                    % extract index of point that matches at current
                    % iteration
                    sp_obs_ren(idx_sp,1) = {i};
                end
                if not(isempty(bl_obs_ren))
                    % extract index of point that matches at current
                    % iteration
                    idx_bl_s = strcmp(bl_obs_ren(:,1),coord_ren(i,1));
                    idx_bl_p = strcmp(bl_obs_ren(:,2),coord_ren(i,1));
                    % extract index of point that matches at current
                    % iteration
                    bl_obs_ren(idx_bl_s,1) = {i};
                    bl_obs_ren(idx_bl_p,2) = {i};
                end
                if not(isempty(constXYZ_ren))
                    % extract index of point that matches at current
                    % iteration
                    idx_conXYZ = strcmp(constXYZ_ren(:,1),coord_ren(i,1));
                    idx_conflh = strcmp(constflh_ren(:,1),coord_ren(i,1));
                    % extract index of point that matches at current
                    % iteration
                    constXYZ_ren(idx_conXYZ,1) = {i};
                    constflh_ren(idx_conflh,1) = {i};
                end
                % change the name also in coordinates list
                coord_ren{i,1} = i;
            end
            
            % store variable in the object
            % azimut
            if ~isempty(A_obs_ren) % verify if there are observations
                A_obs_tmp(:,[1 2 3 4 5 6 8]) = cell2mat(A_obs_ren(:,[1 2 3 4 5 6 8]));
                A_obs_tmp(:,7) = cell2mat(A_obs_ren(:,7));
                obj.A_obs = A_obs_tmp;
                % convert Azimuth angle into clockcounterwise and in the domain
                % -200 to 200
                % angle between 0 and 200 become - A
                obj.A_obs(obj.A_obs(:,3) < 200,3) = - obj.A_obs(obj.A_obs(:,3) < 200,3);
                obj.A_obs(obj.A_obs(:,3) > 200,3) = 400 - obj.A_obs(obj.A_obs(:,3) > 200,3);
                % orientation at left circle observations
                obj.A_obs(obj.A_obs(:,7) == 1,3) = obj.A_obs(obj.A_obs(:,7) == 1,3) - 200;
                % reset the right domain of the angles
                obj.A_obs(obj.A_obs(:,3) < - 200,3) = obj.A_obs(obj.A_obs(:,3) < -200,3) + 400;
                obj.A_obs(obj.A_obs(:,3) > 200,3) = obj.A_obs(obj.A_obs(:,3) > 200,3) - 400;
                % convert units of measurment in order to express angles in
                % radians
                obj.A_obs(:,3) = obj.A_obs(:,3) .* pi ./ 200;  % Azimut obs
                obj.A_obs(:,6) = obj.A_obs(:,6) ./ 3600 .* pi ./ 180;  % Azimut sigma
            else obj.A_obs = ones(0,8); % oterwise allocate a matrix of 0 by 8
            end
            % zenith
            if ~isempty(Z_obs_ren) % verify if there are observations
                Z_obs_tmp(:,[1 2 3 4 5 6 8]) = cell2mat(Z_obs_ren(:,[1 2 3 4 5 6 8]));
                Z_obs_tmp(:,7) = cell2mat(Z_obs_ren(:,7));
                obj.Z_obs = Z_obs_tmp;
                % transform Zenith left circle measuremnt into right circle ones
                obj.Z_obs(obj.Z_obs(:,7) == 1,3) = 400 - obj.Z_obs(obj.Z_obs(:,7) == 1,3);
                % convert units of measurment in order to express angles in
                % radians
                obj.Z_obs(:,3) = obj.Z_obs(:,3) .* pi ./ 200;  % Zenit obs
                obj.Z_obs(:,6) = obj.Z_obs(:,6) ./ 3600 .* pi ./ 180;  % Zenit sigma
            else obj.A_obs = ones(0,8); % oterwise allocate a matrix of 0 by 8
            end
            % distances
            if ~isempty(D_obs_ren) % verify if there are observations
                obj.D_obs = cell2mat(D_obs_ren);
            else obj.D_obs = ones(0,7);% oterwise allocate a matrix of 0 by 7
            end
            % Station list
            if ~isempty(TS_list_ren) % verify if there are observations
                obj.St_list_obs = cell2mat(TS_list_ren);
            else obj.St_list_obs = ones(0,5);% oterwise allocate a matrix of 0 by 5
            end
            % levelling
            if ~isempty(lev_obs_ren) % verify if there are observations
                obj.Dh_obs = cell2mat(lev_obs_ren);
            else obj.Dh_obs = ones(0,4);% oterwise allocate a matrix of 0 by 4
            end
            % GPS single point
            if ~isempty(sp_obs_ren) % verify if there are observations
                obj.GPS_obs = cell2mat(sp_obs_ren);
            else obj.GPS_obs = ones(0,10);% oterwise allocate a matrix of 0 by 10
            end
            % GPS baseline
            if ~isempty(bl_obs_ren) % verify if there are observations
                obj.base_obs = cell2mat(bl_obs_ren);
            else obj.base_obs = ones(0,11);% oterwise allocate a matrix of 0 by 11
            end
            if ~isempty(constXYZ_ren) && ~isempty(constflh_ren)          
                obj.constXYZ = cell2mat(constXYZ_ren);
                obj.constflh = cell2mat(constflh_ren);
                obj.constflh(:,2:3) = obj.constflh(:,2:3) .* pi ./ 180; %convert to radians
            else
                obj.constXYZ = zeros(0,4);
                obj.constflh = zeros(0,4);
            end
            % coordinates
            if ~isempty(coord_ren) % verify if there are values
                obj.c_app = cell2mat(coord_ren(:,2:4));
            else obj.c_app = ones(0,4);% oterwise allocate a matrix of 0 by 4
            end
            % convert the coordinates values if they are present
            if ~nocoord
                obj.c_app(:,[1 2]) = obj.c_app(:,[1 2]) .* pi ./ 180;  % Approx coordinates
            end
        end
        
        % function to compute the approximated coordinates, startig from
        % GPS, levelling and total station coordinates
        function ApproxCoordComputation(obj)
            %try
                % CONSTRAINTS --------------------------------------------
                [~, idx, ~] = unique(obj.constXYZ(:,1));
                const = obj.constXYZ(idx,1:4);
                % GPS SINGLE POINT ---------------------------------------
                % extract unique list of GPS observations
                [~, idx, ~] = unique(obj.GPS_obs(:,1));
                GPS_o = obj.GPS_obs(idx,1:4);
                % GPS_o = [const; GPS_o];
                % [~, idx, ~] = unique(GPS_o(:,1));
                % GPS_o = GPS_o(idx,:);
                % c_glob --> to store global coordinates |name|X|Y|Z|
                c_glob = GPS_o;
                % GPS BASELINES ------------------------------------------
                for i = 1:size(obj.GPS_obs,1) % loop to seek all the master stations
                    % extract baselines from current master station
                    idx = obj.base_obs(:,1) == GPS_o(i,1);
                    base_i = obj.base_obs(idx,:);
                    % get unique observations from current baselines set
                    [~, idx, ~] = unique(base_i(:,2));
                    base_i = base_i(idx,:);
                    % compute global coordinates of the point considered and store them in c_glob
                    c_glob = [c_glob;
                        base_i(:,2) obj.GPS_obs(i,2) + base_i(:,3) obj.GPS_obs(i,3) + base_i(:,4) obj.GPS_obs(i,4) + base_i(:,5)];
                end
                % get unique values of coordinates in case of duplicate
                [~, idx, ~] = unique(c_glob(:,1));
                c_glob = c_glob(idx,:);
                % get the whole list of GPS measured points (baselines and single point)
                GPS_pt = unique([obj.GPS_obs(:,1); obj.base_obs(:,2)]);
                % if global coordinates of some GPS observed points are not computed number of rows
                % of GPS_pt and c_glob is not the same and the if statement is considered
                if size(GPS_pt,1) ~= size(c_glob,1)
                    % find stations not in GPS single point list
                    noGPS_obs = setdiff(unique(obj.base_obs(:,1)),unique(obj.GPS_obs(:,1)));
                    % set an indicator in order to stop the cycle when all the
                    % poitns are computed
                    stop = false;
                    j = 0;
                    while ~stop && j <= size(noGPS_obs,1) % if all the point are computed stop is true
                        % if there are no sufficient GPS observations approximated coordinates cannot be computed
                        stop = true;
                        j = j + 1;
                        for i = 1:size(noGPS_obs)
                            % get all the observations of the current GPS station
                            idx = obj.base_obs(:,1) == noGPS_obs(i);
                            base_i = obj.base_obs(idx,:);
                            % get unique baseline observations
                            [~, idx, ~] = unique(base_i(:,2));
                            base_i = base_i(idx,:);
                            % extract the station coordinates from the approximated coordinates already computed
                            Xo = c_glob(c_glob(:,1) == noGPS_obs(i,1),:);
                            % if the point isn't already present in the approximated coordinates
                            if ~isempty(Xo)
                                % compute global coordinates of the points of this set of baselines
                                c_glob = [c_glob;
                                    base_i(:,2) Xo(1,2) + base_i(:,3) Xo(1,3) + base_i(:,4) Xo(1,4) + base_i(:,5)];
                            else stop = false; % if Xo is not present it could be in the computed during current itetation so the while loop insn't stopped
                            end
                        end
                    end
                    % if the observation are not enough stop the functions
                    if j > size(noGPS_obs,1)
                        %                         errcode = 1;
                        error('GeoNet:ApproxCoordComputation','Not enougth GPS observations or wrong setup of the net');
                    end
                end
                % add constraints
                c_glob = [c_glob; const];
                % get unique values of approximated coordinates
                [~, idx, ~] = unique(c_glob(:,1));
                c_glob = c_glob(idx,:);
                % find the baricenter of curent coordinates set (X,Y,Z and phi,
                % la , h)
                XYZ_bari = mean(c_glob(:,2:4))';
                flh_bari = obj.cart2geo(XYZ_bari',obj.ellipsoid.(obj.SelectedEllips).a,obj.ellipsoid.(obj.SelectedEllips).f)';
                fi_b = flh_bari(1);
                la_b = flh_bari(2);
                % compute rotation matrix
                R = [-sin(la_b) -sin(fi_b)*cos(la_b) cos(fi_b)*cos(la_b);
                    cos(la_b)   -sin(fi_b)*sin(la_b) cos(fi_b)*sin(la_b);
                    0           cos(fi_b)            sin(fi_b)];
                % allocate space for local coordinates
                c_loc = nan(size(c_glob));
                c_loc(:,1) = c_glob(:,1);
                % copmpute local coordinates
                for i=1:size(c_glob,1)
                    c_loc(i,2:4) = (R' * (c_glob(i,2:4)'- XYZ_bari))';
                end
                % CLASSICAL OBSERVATIONS ---------------------------------
                % start to consider classical observations (total station and
                % leveling)
                % planimetry with total station in case of contemporary A, Z, D
                ts_loc_cell = cell(0,1); % initialize the variable ts_cell
                for i = 1:size(obj.St_list_obs,1)
                    % extract observations of the current station
                    Di = obj.D_obs(obj.D_obs(:,7)==i,:);
                    Ai = obj.A_obs(obj.A_obs(:,8)==i,:);
                    Zi = obj.Z_obs(obj.Z_obs(:,8)==i,:);
                    % index in order to remove duplicate values
                    [~, Didx, ~] = unique(Di(:,2));
                    [~, Aidx, ~] = unique(Ai(:,2));
                    [~, Zidx, ~] = unique(Zi(:,2));
                    % extract unique observations
                    Di = Di(Didx,:);
                    Ai = Ai(Aidx,:);
                    Zi = Zi(Zidx,:);
                    [~, Didx, Zidx] = intersect(Di(:,2),Zi(:,2));
                    Di_com = Di(Didx,:);
                    Zi_com = Zi(Zidx,:);
                    [~, DZidx, Aidx] = intersect(Di_com(:,2),Ai(:,2));
                    Di_com = Di_com(DZidx,:);
                    Zi_com = Zi_com(DZidx,:);
                    Ai_com = Ai(Aidx,:);
                    % import name of the points
                    ts_loc = Di(:,2);
                    % compute the local cartesian coordinates (in
                    % Station RF)
                    [ts_loc(:,2), ts_loc(:,3), ts_loc(:,4)] = sph2cart(Ai_com(:,3),pi/2-Zi_com(:,3),Di_com(:,3));
                    % modify the height coordinate based on
                    % instrumental height
                    % if instrumental height are the same do the heigh
                    % correction
                    if sum(Di_com(:,5) ~= Zi_com(:,5)) == 0
                        ts_loc(:,4) = ts_loc(:,4) + Di_com(:,4) - Di_com(:,5);
                    end
                    % add the station point to the coordinates
                    ts_loc = [Di(1,1) 0 0 0; ts_loc];
                    % roto-translate the single station in order to have
                    % coordinates in the local R.F.
                    % if ~isempty(setdiff(ts_loc(:,1),c_loc(:,1))) % verify if there are some points of the station that are already computed in local system
                        % extract the common point between local R.F. and
                        % station R.F.
                        [CP, i_ts, i_cloc] = intersect(ts_loc(:,1),c_loc(:,1));
                        % we need at least 2 points to compute the parameters
                        % otherwise we save the results in station reference
                        % frame
                        if size(CP,1) >= 2
                            % if the station is a CP use it as CP
                            if sum(CP == Di(1,1)) > 0
                                idx = CP == Di(1,1);
                                ts_x1 = ts_loc(i_ts(idx),2);
                                ts_y1 = ts_loc(i_ts(idx),3);
                                ts_z1 = ts_loc(i_ts(idx),4);
                                loc_x1 = c_loc(i_cloc(idx),2);
                                loc_y1 = c_loc(i_cloc(idx),3);
                                loc_z1 = c_loc(i_cloc(idx),4);
                                i_ts(idx) = [];
                                i_cloc(idx) = [];
                            else % otherwise use the first point of the list
                                ts_x1 = ts_loc(i_ts(1),2);
                                ts_y1 = ts_loc(i_ts(1),3);
                                ts_z1 = ts_loc(i_ts(1),4);
                                loc_x1 = c_loc(i_cloc(1),2);
                                loc_y1 = c_loc(i_cloc(1),3);
                                loc_z1 = c_loc(i_cloc(1),4);
                                i_ts(1) = [];
                                i_cloc(1) = [];
                            end
                            % extract coordinates of the second common point
                            ts_x2 = ts_loc(i_ts(1),2);
                            ts_y2 = ts_loc(i_ts(1),3);
                            loc_x2 = c_loc(i_cloc(1),2);
                            loc_y2 = c_loc(i_cloc(1),3);
                            % compute parameters of the transformation
                            [loc_Xo, loc_Yo, theta] = obj.inverse2Drt(ts_x1,ts_y1,ts_x2,ts_y2,loc_x1,loc_y1,loc_x2,loc_y2);
                            % built rotation matrix and translation vector
                            R_i = [cos(theta) -sin(theta);
                                sin(theta)  cos(theta)];
                            T = [loc_Xo; loc_Yo];
                            % execute the roto-translation and recover z
                            % coordinate
                            for j = 1:size(ts_loc,1)
                                c_loc_i(j,1) = ts_loc(j,1);
                                c_loc_i(j,2:3) = (T + R_i * ts_loc(j,2:3)')';
                                c_loc_i(j,4) = ts_loc(j,4) - ts_z1 + loc_z1;
                            end
                            % get unique values of local system coordinates
                            c_loc = [c_loc; c_loc_i];
                            [~, idx, ~] = unique(c_loc(:,1));
                            c_loc = c_loc(idx,:);
                            beta_app(i) = theta;
                        else
                            % add the row of the current station becouse
                            % it is not computed in ts_loc_cell
                            ts_loc_cell{end+1,1} = ts_loc;
                            ts_loc_cell{end,2} = i;
                        end
                    % end
                end
                % verify is there are some stations not roto-translated yet
                if ~isempty(ts_loc_cell)
                    stop = false; % flag used to stop the loop when needed
                    k = 0;
                    kmax = size(ts_loc_cell,1);
                    while ~stop && k <=  kmax % if all the point are computed stop is true
                        % if there are no sufficient CP coordinates cannot be
                        % computed (the maximum number of iteration is overtaken
                        stop = true;
                        k = k + 1;
                        for i = size(ts_loc_cell,1):-1:1
                            ts_loc = ts_loc_cell{i,1};
                            if ~isempty(ts_loc) % if the local coordinates of the current station are not computed yet
                                % extract the common point between local R.F. and
                                % station R.F.
                                [CP, i_ts, i_cloc] = intersect(ts_loc(:,1),c_loc(:,1));
                                % we need at least 2 points to compute the parameters
                                % otherwise we save the results in station reference
                                % frame
                                if size(CP,1) >= 2
                                    % extract coordinates of the first common point
                                    ts_x1 = ts_loc(i_ts(1),2);
                                    ts_y1 = ts_loc(i_ts(1),3);
                                    ts_z1 = ts_loc(i_ts(1),4);
                                    loc_x1 = c_loc(i_cloc(1),2);
                                    loc_y1 = c_loc(i_cloc(1),3);
                                    loc_z1 = c_loc(i_cloc(1),4);
                                    % extract coordinates of the second common point
                                    ts_x2 = ts_loc(i_ts(2),2);
                                    ts_y2 = ts_loc(i_ts(2),3);
                                    loc_x2 = c_loc(i_cloc(2),2);
                                    loc_y2 = c_loc(i_cloc(2),3);
                                    % compute parameters of the tranformation
                                    [loc_Xo, loc_Yo, theta] = obj.inverse2Drt(ts_x1,ts_y1,ts_x2,ts_y2,loc_x1,loc_y1,loc_x2,loc_y2);
                                    % built rotation matrix and tranlation vecto
                                    R_i = [cos(theta) -sin(theta);
                                        sin(theta)  cos(theta)];
                                    T = [loc_Xo; loc_Yo];
                                    % execute the roto-translation and recover z
                                    % coordinate
                                    for j = 1:size(ts_loc,1)
                                        c_loc_i(j,1) = ts_loc(j,1);
                                        c_loc_i(j,2:3) = (T + R_i * ts_loc(j,2:3)')';
                                        c_loc_i(j,4) = ts_loc(j,4) - ts_z1 + loc_z1;
                                    end
                                    % get unique values of local system coordinates
                                    c_loc = [c_loc; c_loc_i];
                                    [~, idx, ~] = unique(c_loc(:,1));
                                    c_loc = c_loc(idx,:);
                                    beta_app(ts_loc_cell{i,2}) = theta;
                                    ts_loc_cell(i,:) = [];
                                    % ts_loc_cell{i,1} = zeros(0);
                                else
                                    stop = false; % if not enough CP are present the while loop insn't stopped
                                end
                            end
                        end
                    end
                end
                % verify if there are points with only angular observations
                comp_pt_list = sort(c_loc(:,1));
                obs_pt_list = unique([obj.A_obs(:,2);
                    obj.Z_obs(:,2);
                    obj.D_obs(:,2);
                    obj.GPS_obs(:,1);
                    obj.base_obs(:,2);
                    obj.Dh_obs(:,2)]);
                if size(comp_pt_list,1) < size(obs_pt_list,1)
                    % only azimuthal observations
                    missed = setdiff(obs_pt_list,comp_pt_list);
                    for i = 1: size(missed,1)
                        idx = obj.A_obs(:,2) == missed(i);
                        A_i = obj.A_obs(idx,:);
                        [~, idx, ~] = unique(A_i(:,1:2),'rows');
                        A_i = A_i(idx,:);
                        if size(A_i,1) < 2
                            %                             errcode = 2;
                            error('GeoNet:ApproxCoordComputation','Points with not enough angular observations');
                        else
                            A_i = A_i(:,1:2);
                            p = A_i(1,1);
                            q = A_i(2,1);
                            ip = c_loc(:,1) == p;
                            iq = c_loc(:,1) == q;
                            xp = c_loc(ip,2);
                            yp = c_loc(ip,3);
                            xq = c_loc(iq,2);
                            yq = c_loc(iq,3);
                            Dpq = sqrt((xp - xq).^2+(yp - yq).^2);
                            theta_pq = atan2(yq-yp,xq-xp);
                            theta_qp = atan2(yp-yq,xp-xq);
                            beta_p = beta_app(A_i(ip,8));
                            beta_q = beta_app(A_i(iq,8));
                            alpha_pi = A_i(ip,3) - beta_p;
                            alpha_qi = A_i(iq,3) - beta_q;
                            ipq = alpha_pi - theta_pq;
                            iqp = alpha_qi - theta_qp;
                            piq = pi - ipq - iqp;
                            Dpi = Dpq / sin(piq) * sin(iqp);
                            c_loc(end+1,1) = missed(i);
                            c_loc(end,2) = xp + Dpi * cos(alpha_pi);
                            c_loc(end,3) = yp + Dpi * sin(alpha_pi);
                        end
                        % levelling observations and only zenithal observations
                        idx = obj.Z_obs(:,2) == missed(i);
                        Z_i = obj.Z_obs(idx,:);
                        [~, idx, ~] = unique(Z_i(:,[1 2]),'rows');
                        Z_i = Z_i(idx,:);
                        if size(Z_i,1) < 1
                            idx = obj.lev_obs(:,2) == missed(i);
                            lev_i = obj.lev_obs(idx,:);
                            [~, idx, ~] = unique(lev_i(:,[1 2]),'rows');
                            lev_i = lev_i(idx,:);
                            if size(lev_i,1) < 1
                                %                                 errcode = 3;
                                error('GeoNet:ApproxCoordComputation','Points with not enough vertical distances observations');
                            else
                                p = lev_i(1,1);
                                ip = c_loc(:,1) == p;
                                ii = c_loc(:,1) == lev_i(1,2);
                                zp = c_loc(ip,4);
                                c_loc(ii,5) = zp + lev_i(1,3);
                            end
                        else
                            ip = c_loc(:,1) == Z_i(1,1);
                            xp = c_loc(ip,2);
                            yp = c_loc(ip,3);
                            zp = c_loc(ip,4);
                            ii = c_loc(:,1) == Z_i(1,2);
                            xi = c_loc(ii,2);
                            yi = c_loc(ii,3);
                            Dpi = sqrt((xp - xi).^2+(yp - yi).^2);
                            c_loc(ii,5) = zp + Dpi / tan(Z_i(1,3)) + Z_i(1,4) - Z_i(1,5);  % verificare indici zenit
                        end
                    end
                end
                % store computed results in the object matrix
                c_glob_XYZ = nan(size(c_loc));
                c_glob_XYZ(:,1) = c_loc(:,1);
                for i = 1:size(c_loc,1)
                    c_glob_XYZ(i,2:4) = (XYZ_bari + R * c_loc(i,2:4)')';
                end
                % sort the matrix of coordinates
                [~, idx] = sort(c_glob_XYZ(:,1));
                c_glob_XYZ = c_glob_XYZ(idx,:);
                % compute fi, la, h from X, Y, Z
                c_glob_flh = obj.cart2geo(c_glob_XYZ(:,2:4),obj.ellipsoid.(obj.SelectedEllips).a,obj.ellipsoid.(obj.SelectedEllips).f);
                c_glob_flh = [c_glob_XYZ(:,1) c_glob_flh(:,1:2).*180./pi c_glob_flh(:,3)];
                if size(c_glob_flh,1) == size(obj.coord,1)
                    obj.removeAdjustment;                                       % remove data from the previous adjustment
                    % store them in the global variables
                    obj.coord(:,5:7) = num2cell(c_glob_flh(:,2:4));
                    obj.coord(:,2:4) = num2cell(c_glob_XYZ(:,2:4));
                    obj.c_app = [c_glob_flh(:,2:3) .* pi ./ 180 c_glob_flh(:,4)];
                    % recover the code of the points
                    p_name_code = [obj.sp_obs(:,2) obj.sp_obs(:,6);
                        obj.bl_obs(:,3) obj.bl_obs(:,7);
                        obj.TS_obs(:,2) obj.TS_obs(:,13);
                        obj.constraints(:,2) obj.constraints(:,9)];
                    [~, idx] = unique(p_name_code(:,1));
                    obj.coord(:,8) = p_name_code(idx,2);
                    obj.St_list_obs(:,4) = beta_app(:);
                    obj.TS_list(:,4) = num2cell(beta_app(:));
                    obj.Saved = 0;                                              % set saved to 0
                    set(obj.hh.pVisualization,'SelectedObject',obj.hh.rCoord)   % set active the observation radio button
                    obj.GUI_typeVis = 3;                                        % set the visualization of observation
                    obj.GUI_coord = 1;                                          % set the kind of coordinates to visualize
                    obj.setVisualiPanel;                                        % set the home based on the given informations
                    msgbox('Approximated coordinates correctly computed','Done')
                else
                    %                     errcode = 0;
                    error('error');
                end
            %catch ME
                %                 switch errcode
                %                     case 0
                %                         fprintf('Error in the computation');
                %                     case 1
                %                         fprintf('Not enougth GPS observations or wrong setup of the net');
                %                     case 2
                %                         fprintf('Points with not enough angular observations');
                %                     case 3
                %                         fprintf('Points with not enough angular observations');
                %                 end
%                 fprintf(['Error in ' ME.stack.file ' at line ' ME.stack.line '\n']);
%                 fprintf(ME.message);
%                 obj.coord = cell(0,18);
%                 obj.c_app = ones(0,4);
%                 obj.removeAdjustment;
%                 obj.Saved = 0;                                              % set saved to 0
%                 set(obj.hh.pVisualization,'SelectedObject',obj.hh.rCoord)   % set active the observation radio button
%                 obj.GUI_typeVis = 3;                                        % set the visualization of observation
%                 obj.GUI_coord = 1;                                          % set the kind of coordinates to visualize
%                 obj.setVisualiPanel;                                        % set the home based on the given informations
%                 msgbox('Approximated coordinates cannot be computed with this configuration','Error','error');
            %end
        end
        
        % function to compute bessel correction
        function Bessel(obj)
            for i = 1:size(obj.St_list_obs,1)
                Z_o = obj.Z_obs(obj.Z_obs(:,8) == i,:);        % extract Z_obs for the current station
                A_o = obj.A_obs(obj.A_obs(:,8) == i,:);        % extract A_obs for the current station
                Z_o_list = unique(Z_o(:,[1 2]),'rows');        % etract the lsit of observations done
                A_o_list = unique(A_o(:,[1 2]),'rows');        % etract the lsit of observations done
                Z_couples = zeros(0,2);                        % initialize variables to store couples of zenith and azimuth
                A_couples = zeros(0,7);
                % extract all the couples of zenith observations
                for j = 1:size(Z_o_list,1)
                    obs = Z_o_list(j,:);   % current observation
                    idx_lc = (obs(:,1)==Z_o(:,1)) & (obs(:,2)==Z_o(:,2)) & (0==Z_o(:,7));  % index of left circle observation
                    idx_rc = (obs(:,1)==Z_o(:,1)) & (obs(:,2)==Z_o(:,2)) & (1==Z_o(:,7));  % index of right circle observation
                    if ~(sum(idx_lc)==0 || sum(idx_rc)==0)
                        idx_lc = (idx_lc + 0) .* (1:length(idx_lc))';
                        idx_rc = (idx_rc + 0) .* (1:length(idx_rc))';
                        idx_lc(idx_lc==0) = [];
                        idx_rc(idx_rc==0) = [];
                        [LC,RC] = meshgrid(idx_lc,idx_rc);
                        Z_couples = [Z_couples;
                            Z_o(LC(:),3) Z_o(RC(:),3)];
                    end
                end
                % compute the correction
                Zcorrection = (Z_couples(:,1) - Z_couples(:,2))./2;
                meanZc = mean(Zcorrection);
                stdZc = std(Zcorrection);
                % correct the observations
                LCZ_idx = obj.Z_obs(:,7)==0 & obj.Z_obs(:,8)==i;  % extract left circle measure of the current station
                obj.Z_obs(LCZ_idx,3) = obj.Z_obs(LCZ_idx,3) - meanZc;
                RCZ_idx = obj.Z_obs(:,7)==1 & obj.Z_obs(:,8)==i;  % extract rigth circle measure of the current station
                obj.Z_obs(RCZ_idx,3) = obj.Z_obs(RCZ_idx,3) + meanZc;
                % store the correction and their variance
                obj.TS_list{i,5} = meanZc * 200 / pi;
                obj.TS_list{i,6} = stdZc * 200 / pi;
                obj.St_list_obs(i,4) = meanZc;
                % extract all the couple of Azimuthal observations
                for j = 1:size(A_o_list,1)
                    obs = A_o_list(j,:);   % current observation
                    idx_lc = (obs(:,1)==A_o(:,1)) & (obs(:,2)==A_o(:,2)) & (0==A_o(:,7));  % index of left circle observation
                    idx_rc = (obs(:,1)==A_o(:,1)) & (obs(:,2)==A_o(:,2)) & (1==A_o(:,7));  % index of right circle observation
                    if ~(sum(idx_lc)==0 || sum(idx_rc)==0)
                        idx_lc = (idx_lc + 0) .* (1:length(idx_lc))';
                        idx_rc = (idx_rc + 0) .* (1:length(idx_rc))';
                        idx_lc(idx_lc==0) = [];
                        idx_rc(idx_rc==0) = [];
                        [LC,RC] = meshgrid(idx_lc,idx_rc);
                        meanAci = mean(A_o(LC(:),3) - A_o(RC(:),3));
                        A_couples = [A_couples;
                            repmat(obs,length(LC(:)),1) LC(:) RC(:) A_o(LC(:),3) A_o(RC(:),3) repmat(meanAci,length(LC(:)),1)];
                    end
                end
                % compute the correction
                Acorrection = (A_couples(:,5) - A_couples(:,6))./2;
                meanAc = mean(Acorrection);
                stdAc = std(Acorrection);
                % correct the observations
                LCA_idx = obj.A_obs(:,7)==0 & obj.A_obs(:,8)==i;  % extract left circle measure of the current station
                obj.A_obs(LCA_idx,3) = obj.A_obs(LCA_idx,3) - meanAc;
                RCA_idx = obj.A_obs(:,7)==1 & obj.A_obs(:,8)==i;  % extract rigth circle measure of the current station
                obj.A_obs(RCA_idx,3) = obj.A_obs(RCA_idx,3) + meanAc;
                % store the correction and their variance
                obj.TS_list{i,7} = meanAc * 200 / pi;
                obj.TS_list{i,8} = stdAc * 200 / pi;
                obj.St_list_obs(i,5) = meanAc;
            end
        end
        
        % function to compute approximated beta in a global environment
        function BetaAppGlob(obj)
            % compute the approximated orientation angles
            % Useful functions -------------------------------------------
            % - Geodetiche --> geocentriche ----- | X | Y | Z | -----------------------
            fX = @(fi,la,h,N,e2) [(N+h).*cos(fi).*cos(la), (N+h).*cos(fi).*sin(la), (N*(1-e2)+h).*sin(fi)];
            % - efi ----- | efiX | efiY | efiZ | --------------------------------------
            fefi = @(fi,la) [-sin(fi).*cos(la), -sin(fi).*sin(la), cos(fi)];
            % - ela ----- | elaX | elaY | elaZ | --------------------------------------
            fela = @(la) [-sin(la), cos(la), zeros(size(la,1),1)];
            % - Grannormale -----------------------------------------------------------
            fN = @(fi,a,e2) a./(sqrt(1-e2*(sin(fi).^2)));
            % import ellipsoidal dimensions
            a = obj.ellipsoid.(obj.SelectedEllips).a;
            f = obj.ellipsoid.(obj.SelectedEllips).f;
            e2 = 1 - (1-f)^2;
            % verify if there areazimutal observationa and approximated
            % coordinates
            if ~isempty(obj.A_obs) && size(obj.St_list_obs,1)>0 ...
                    && size(obj.c_app,1)>0
                beta = zeros(size(obj.St_list_obs,1),1);
                % loop for all the stations
                for i = 1:size(obj.St_list_obs,1)
                    idx = obj.A_obs(:,8) == i;
                    Ai = obj.A_obs(idx,:);
                    p = Ai(1,1);
                    q = Ai(1,2);
                    fip = obj.c_app(p,1);
                    lap = obj.c_app(p,2);
                    hp = obj.c_app(p,3);
                    fiq = obj.c_app(q,1);
                    laq = obj.c_app(q,2);
                    hq = obj.c_app(q,3);
                    Np = fN(fip, a, e2);
                    Nq = fN(fiq, a, e2);
                    Xp = fX(fip, lap, hp, Np, e2);
                    Xq = fX(fiq, laq, hq, Nq, e2);
                    efip = fefi(fip,lap);
                    elap = fela(lap);
                    Dpq = Xq-Xp;
                    t = atan2(Dpq * efip',Dpq * elap');
                    beta(i) = t - Ai(1,3);
                    if beta(i) > pi
                        beta(i) = beta(i) - 2*pi;
                    elseif beta(i) < -pi
                        beta(i) = 2*pi + beta(i);
                    end
                end
                % store the result
                obj.St_list_obs(:,4) = beta;
            end
            
        end
        
        % function to launch the adjustment
        function launchAdj(obj)
            obj.getAdjOpt
            delete(obj.oh.fAdjOpt);
            obj.NameTranslation;
            switch obj.AdjType
                case 1 % 3D global network without contraints
                    obj.BetaAppGlob;
                    alfa = obj.LSoptions.alfa;
                    beta = obj.LSoptions.beta;
                    GPSw = obj.LSoptions.GPSw;
                    imax = obj.LSoptions.imax;
                    [flh, Cff, obs_rms, ts_list, chi2, ltest, intreal, extreal, lred, icurr, convOK, sigma02, v, vnorm] = ...
                        obj.gloAdj(obj, obj.c_app, obj.D_obs,...
                        obj.Z_obs, obj.A_obs, obj.St_list_obs, obj.Dh_obs,...
                        obj.GPS_obs, obj.base_obs, ...
                        obj.ellipsoid.(obj.SelectedEllips).a, obj.ellipsoid.(obj.SelectedEllips).f,...
                        obj.constXYZ, alfa, beta, GPSw, imax);
                    if convOK == 1
                        obj.adjusted = 1;
                        obj.Saved = 0;
                        % geocentric cartesian coordinates [m]
                        XYZ = obj.geo2cart(flh,obj.ellipsoid.(obj.SelectedEllips).a, obj.ellipsoid.(obj.SelectedEllips).f);
                        Cxx = obj.Cff2Cxx(Cff, num2cell(flh), obj.ellipsoid.(obj.SelectedEllips).a, obj.ellipsoid.(obj.SelectedEllips).f);
                        % East-west curvature radius, meriadian radius and mean
                        % radius in each point [m]
                        N = obj.fN(flh(:,1),obj.ellipsoid.(obj.SelectedEllips).a, 1-(1-obj.ellipsoid.(obj.SelectedEllips).f)^2);
                        M = obj.fN(flh(:,1),obj.ellipsoid.(obj.SelectedEllips).a, 1-(1-obj.ellipsoid.(obj.SelectedEllips).f)^2);
                        R = sqrt(M .* N) + flh(:,3);
                        % standard deviation of fi, la, h expressed in [m]
                        std_f = R .* cell2mat(cellfun(@(x) sqrt(x(1,1)), Cff, 'UniformOutput', 0));
                        std_l = R .* cos(flh(:,1)) .* cell2mat(cellfun(@(x) sqrt(x(2,2)), Cff, 'UniformOutput', 0));
                        std_h = cell2mat(cellfun(@(x) sqrt(x(3,3)), Cff, 'UniformOutput', 0));
                        Cxx = cellfun(@(x) [x(1,1) x(1,2) x(1,3) x(2,2) x(2,3) x(3,3)], Cxx, 'UniformOutput', 0);
                        % external relaibility in fi, la, h
                        extreal_flh = [R .* extreal(:,1) R .* cos(flh(:,1)) .* extreal(:,2) extreal(:,3)];
                        % external reliabiliy in X, Y, Z
                        efi = [-sin(flh(:,1)).*cos(flh(:,2)) -sin(flh(:,1)).*sin(flh(:,2)) cos(flh(:,1))];
                        % - ela ----- | elaX | elaY | elaZ | --------------------------------------
                        ela =[-sin(flh(:,2)) cos(flh(:,2)) zeros(size(flh(:,2),1),1)];
                        % - nu ----- | nuX | nuY | nuZ | ------------------------------------------
                        nu = [cos(flh(:,1)).*cos(flh(:,2)) cos(flh(:,1)).*sin(flh(:,2)) sin(flh(:,1))];
                        % extreal_XYZ = efi .* repmat((N + flh(:,3)) .* extreal(:,1),1,3) + ela .* repmat((M + flh(:,3)).* extreal(:,2),1,3) + nu .* repmat(extreal(:,3),1,3);
                        extreal_XYZ = abs(efi .* repmat(extreal_flh(:,1),1,3) + ela .* repmat(extreal_flh(:,2),1,3) + nu .* repmat(extreal_flh(:,3),1,3));
                        extreal_XYZ(extreal==Inf) = Inf;
                        flh(:,1:2) = flh(:,1:2) .* 180 ./ pi; % unit conversion (rad --> deg)
                        % store coordinates and variances
                        uc = logical(cell2mat(obj.coord(:,18)));
                        obj.coord(uc,2:4) = num2cell(XYZ);
                        obj.coord(uc,5:7) = num2cell(flh);
                        obj.coord(uc,9:11) = num2cell([std_f std_l std_h].*1e3);
                        obj.coord(uc,12:17) = num2cell(cell2mat(Cxx));
                        obj.coord(uc,19:24) = num2cell([extreal_flh extreal_XYZ]);
                        % store total station results
                        if size(obj.TS_obs,1) ~= 0
                            uA = cell2mat(obj.TS_obs(:,4));
                            uZ = cell2mat(obj.TS_obs(:,6));
                            uD = cell2mat(obj.TS_obs(:,8));
                            obj.TS_obs(uA,18) = num2cell(obs_rms.A.*180./pi.*3600);
                            obj.TS_obs(uZ,19) = num2cell(obs_rms.Z.*180./pi.*3600);
                            obj.TS_obs(uD,20) = num2cell(obs_rms.D);
                            obj.TS_obs(uA,21) = num2cell(ltest.A);
                            obj.TS_obs(uZ,22) = num2cell(ltest.Z);
                            obj.TS_obs(uD,23) = num2cell(ltest.D);
                            obj.TS_obs(uA,24) = num2cell(lred.A);
                            obj.TS_obs(uZ,25) = num2cell(lred.Z);
                            obj.TS_obs(uD,26) = num2cell(lred.D);
                            obj.TS_obs(uA,27) = num2cell(intreal.A);
                            obj.TS_obs(uZ,28) = num2cell(intreal.Z);
                            obj.TS_obs(uD,29) = num2cell(intreal.D);
                            obj.TS_obs(uA,30) = num2cell(v.A);
                            obj.TS_obs(uZ,31) = num2cell(v.Z);
                            obj.TS_obs(uD,32) = num2cell(v.D);
                            obj.TS_obs(uA,33) = num2cell(vnorm.A);
                            obj.TS_obs(uZ,34) = num2cell(vnorm.Z);
                            obj.TS_obs(uD,35) = num2cell(vnorm.D);
                            obj.TS_list(:,[4 5 7]) = num2cell(ts_list(:,3:5));
                        end
                        % store levelling results
                        if size(obj.lev_obs,1) ~= 0
                            uL = cell2mat(obj.lev_obs(:,1));
                            obj.lev_obs(uL,6) = num2cell(obs_rms.lev);
                            obj.lev_obs(uL,7) = num2cell(ltest.lev);
                            obj.lev_obs(uL,8) = num2cell(lred.lev);
                            obj.lev_obs(uL,9) = num2cell(intreal.lev);
                            obj.lev_obs(uL,10) = num2cell(v.lev);
                            obj.lev_obs(uL,11) = num2cell(vnorm.lev);
                        end
                        % store GPS baseline results
                        if size(obj.bl_obs,1) ~= 0
                            uB = cell2mat(obj.bl_obs(:,1));
                            obj.bl_obs(uB,14:16) = num2cell(obs_rms.base);
                            obj.bl_obs(uB,17:19) = num2cell(ltest.base);
                            obj.bl_obs(uB,20:22) = num2cell(lred.base);
                            obj.bl_obs(uB,23:25) = num2cell(intreal.base);
                            obj.bl_obs(uB,26:28) = num2cell(v.base);
                            obj.bl_obs(uB,29:31) = num2cell(vnorm.base);
                        end
                        % store GPS baseline point results
                        if size(obj.sp_obs,1) ~= 0
                            uG = cell2mat(obj.sp_obs(:,1));
                            obj.sp_obs(uG,13:15) = num2cell(obs_rms.gps);
                            obj.sp_obs(uG,16:18) = num2cell(ltest.gps);
                            obj.sp_obs(uG,19:21) = num2cell(lred.gps);
                            obj.sp_obs(uG,22:24) = num2cell(intreal.gps);
                            obj.sp_obs(uG,25:27) = num2cell(v.gps);
                            obj.sp_obs(uG,28:30) = num2cell(vnorm.gps);
                        end
                        % store LS statistics
                        obj.LSreport.iconv = icurr;
                        obj.LSreport.chi2 = chi2;
                        obj.LSreport.sigma02 = sigma02;
                        % display results
                        CreateStruct.Interpreter = 'tex';
                        CreateStruct.WindowStyle = 'modal';
                        h = msgbox(sprintf('Convergence reached in %d iterations \n \n Estimated r.m.s. is %.2f',icurr,sqrt(sigma02)),'Done',CreateStruct);
                        % visualize the resutls
                        obj.GUI_typeVis = 3;
                        obj.GUI_coord = 2;
                        obj.setCoordActive     % set the coordinates panel active
                        obj.setObsResInactive  % set the observations-results panel inactive
                        set(obj.hh.pVisualization,'SelectedObject',obj.hh.rCoord);
                        obj.setVisualiPanel;                                        % set the home based on the given informations
                        uiwait(h)
                        obj.unlockHome;
                        set(obj.hh.bGraphic,'Enable','on');
                    else h = msgbox('Verify network configuration','error','Error');
                        uiwait(h);
                        obj.unlockHome;
                    end
                case 2 % 3D global network with contraints
                case 3 % 3D local network with constraints
                case 4 % 2D local network with constraints
                case 5 % GPS levelling netwotk
            end
        end
    end % end of computation methods
    
    
    % Static function %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    methods(Static)
        % function to close the software
        function closeGeoNet(obj,hObject)
            if ~isempty(obj) % check if the object has been cleared
                switch obj.Saved % check if current data are saved
                    case 0 % the job isn't saved
                        savejob = questdlg('Do you want to save modify to the current job, before closing GeoNet?'); % if not ask if oen want to save the job
                        % evaluate the decision
                        switch savejob     % evaluate the possiblity if the user doesn't try to cance operation
                            case 'Yes'     % if it yes save the job, also do nothing and go on to close the software
                                saveOK = obj.SaveData;
                                if saveOK == 0
                                    return
                                end
                                delete(hObject);
                                if ishandle(obj.gh)
                                    delete(obj.gh);
                                end
                                global geoentGUI %#ok<TLEV,NUSED>
                                clearvars -global geonetGUI;
                            case 'No'
                                delete(hObject);
                                if ishandle(obj.gh)
                                    delete(obj.gh);
                                end
                                global geoentGUI %#ok<REDEF,TLEV>
                                clearvars -global geonetGUI;
                            case 'Cancel'  % if the user abort the operation stop the esecution of the function
                                return
                        end
                        
                    case 1 % the job is saved and so the software can be closed
                        delete(hObject);
                        if ishandle(obj.gh)
                            delete(obj.gh);
                        end
                        global geoentGUI %#ok<TLEV>
                        clearvars -global geonetGUI;
                end
            else % if the object has been cleared kill it
                delete(hObject);
            end
        end
        
        % function to import a txt files and reorder fileds, based on a
        % variable that tell us where is the place of each column
        function txt = importTXT(filename,delimiter,order,header)
            fid = fopen(filename,'r'); % open the file in read only mode
            field = [];                % variable where the field sequence must be stored
            
            % create the field sequence, based on the length of ord (total number of
            % field) and counting if there ore some empty filed at the end of the list
            for i=1:length(order)
                if sum(order(i:end))~=0 % check if the field from i to end are empty (0 means empty)
                    field = strcat(field,'%s');  % if they aren't empty add %s to the variable field
                end % end of if
            end % end of for
            
            % import the data using matlab built in function
            ReadData = textscan(fid, field, 'delimiter', delimiter, 'HeaderLines', header); % cell where each column contain a cell array with the whole column
            ImportedData = cell(size(ReadData{1,1},1),size(ReadData,2)); % initializing the variable where imported data are stored
            % extract dimension of the first column
            dim = size(ReadData{1,1});
            % store data in a cell array where each row is a row o the original file
            for i = 1:size(ReadData,2)
                % check if all the column has the same dimension, otherwise
                % stop the function
                if (size(ReadData{1,1},1) == dim(1)) && (size(ReadData{1,1},2)== dim(2))
                    ImportedData(:,i) = ReadData{1,i};
                else msgbox('There are some column without right dimension','Error','error');
                    fclose(fid);
                    return
                end
            end
            
            nrows = size(ImportedData,1);            % number of row of imported data
            ncols = length(order);                   % number of column of imported data
            txt = cell(nrows, ncols);                % initializing a cell matrix to contai all the rows and columns extracted from the file
            
            % reorder imported column in cell matrix
            txt(:, order(order~=0)) = ImportedData;
            % remove initial and final spaces
            txt = strtrim(txt);
            
            % close the file
            fclose(fid);
        end % end of importTXT
        
        % function to compute East-West curvature radius
        function N = fN(fi, a, e2)
            N = a./(sqrt(1-e2.*(sin(fi).^2)));
        end
        
        % function to compute Meridian radius
        function M = fM(fi, a, e2)
            M = (a.*(1-e2))./(1-e2.*sin(fi).^2).^(3/2);
        end
        
        % function to convert a vector of geodetic coordinates into
        % cartesia geocentric coordinates whit a given ellipsoid, parametrs
        % a (major semi-axes) and f (flattening)
        function XYZ = geo2cart(flh,a,f)
            % coordinates input must be in radians and meter and it need to
            % be a matrix with phi on the first column, lambda on the
            % second column and ellipsoidal heigth on the third one.
            % a is expressed in m and f is without measurement unit.
            % Output will be in meters
            fi = flh(:,1);  % extract fi
            la = flh(:,2);  % extract lambda
            h = flh(:,3);   % extract h
            % compute primary eccentricity
            e2 = 1 - (1 - f).^2;
            % East-West curvature radius
            N = a ./ sqrt(1 - e2 .* sin(fi).^2);
            % compute the cartesian coordinate X, Y, Z
            X = (N + h) .* cos(fi) .* cos(la);
            Y = (N + h) .* cos(fi) .* sin(la);
            Z = (N .* (1 - e2) + h) .* sin(fi);
            XYZ = [X Y Z]; % create the vector containing the 3 transformed coordinates
        end
        
        % function to convert a vector of cartesian geocentric coordinates
        % into geodetic coordinates using a given ellipsoid, with parametrs
        % a (major semi-axes) and f (flattening)
        function flh = cart2geo(XYZ,a,f)
            % coordinates input must be in meters and they  need to
            % be a matrix with X on the first column, Y on the second one,
            % and Z on the third one.
            % a is expressed in m and f is without measurement unit.
            % Output will be in radians and meters
            X = XYZ(:,1);
            Y = XYZ(:,2);
            Z = XYZ(:,3);
            b = a .* (1 - f); % compute the minor semi-axes b
            % compute primary eccentricity
            e2 = (a^2 - b^2) / a^2;
            % compute the secondary eccentricity
            e2b = (a^2 - b^2) / b^2;
            rho = sqrt(X.^2 + Y.^2);
            psi = atan2(Z,(rho .* sqrt(1-e2)));
            la = atan2(Y,X);
            fi = atan2(Z + e2b .* b .* sin(psi).^3,...
                rho - e2 .* a .* cos(psi).^3);
            % East-West curvature radius
            N = a ./ sqrt(1 - e2 .* sin(fi).^2);
            h = rho ./ cos(fi) - N;
            flh = [fi la h];
        end
        
        % XYZ --> east, north, up
        function [ENU, Cee, R, T] = xyz2enu(XYZ, a, f, XYZo, Cxx)
            % compute east, north, up coordinates starting from geocentric
            % coordinates setting the option in point XYZo
            % input:
            % - XYZ  --> matrix with |X|Y|Z|
            % - a    --> ellipsoid semimajor axis
            % - f    --> flattening of the ellipsoid
            % - XYZ  --> coorinates of the origin |Xo|Yo|Zo|
            % - Cxx  --> cell array of the covariance matrixes (3x3)
            
            % extract XYZ coordinate
            X = num2cell(XYZ(:,1));
            Y = num2cell(XYZ(:,2));
            Z = num2cell(XYZ(:,3));
            XYZ = cellfun(@(x,y,z) [x; y; z],X, Y, Z, 'UniformOutput', 0);
            % extract XYZ origin coordinate
            Xo = XYZo(:,1);
            Yo = XYZo(:,2);
            Zo = XYZo(:,3);
            b = a .* (1 - f); % compute the minor semi-axes b
            % compute primary eccentricity
            e2 = (a^2 - b^2) / a^2;
            % compute the secondary eccentricity
            e2b = (a^2 - b^2) / b^2;
            rho = sqrt(Xo.^2 + Yo.^2);
            psi = atan2(Zo,(rho .* sqrt(1-e2)));
            % compute fi and lambda of the origin
            lao = atan2(Yo,Xo);
            fio = atan2(Zo + e2b .* b .* sin(psi).^3,...
                rho - e2 .* a .* cos(psi).^3);
            % rotation matrix and translation term
            R = [-sin(lao) -sin(fio)*cos(lao) cos(fio)*cos(lao);
                 cos(lao)  -sin(fio)*sin(lao) cos(fio)*sin(lao); 
                 0         cos(fio)           sin(fio)];
            T = [Xo; Yo; Zo];
            % compute the transformation
            ENU = cellfun(@(x) (R'*(x - T))', XYZ, 'UniformOutput', 0);
            % propagate the variance
            Cee = cellfun(@(Cxx) R'*Cxx*R, Cxx, 'UniformOutput', 0);
            % convert to matrix
            ENU = cell2mat(ENU);
        end
        
        % convert a formatted sexadecimal
        function dec = sex2dec(sex)
            pos = [strfind(sex,'°'), strfind(sex,''''), strfind(sex,'"')]; % find the place of the charateristic delimiter of the sexagesimal expression
            dec = str2double(sex(1:pos(1)-1)) + str2double(sex(pos(1)+1:pos(2)-1)) / 60 + str2double(sex(pos(2)+1:pos(3)-1)) / 3600; % execute the conversione
        end
        
        % convert sexadecimal angle to formatted sexagesimal
        function sex = dec2sex(dec)
            deg = fix(dec);                         % extract the degree
            min = fix((dec - deg) .* 60);           % extract minutes
            sec = (dec - deg - min / 60) .* 3600;   % extract seconds
            sex = strcat(sprintf('%d',deg),'° ',...
                sprintf('%d',min),''' ',...
                sprintf('%.5f',sec),'"');
        end
        
        % function to convert covariance matrix from geodetic coordinates
        % to cartesian coordinates (check if it works)
        function Cxx = Cff2Cxx(Cff, flh, a, f)
            % - Grannormale -----------------------------------------------------------
            fN = @(fi,a,e2) a./(sqrt(1-e2.*(sin(fi).^2)));
            % - M ---------------------------------------------------------------------
            fM = @(fi,a,e2) (a.*(1-e2))./(1-e2.*sin(fi).^2).^(3/2);
            % - efi ----- | efiX | efiY | efiZ | --------------------------------------
            fefi = @(fi,la) [-sin(fi).*cos(la); -sin(fi).*sin(la); cos(fi)];
            % - ela ----- | elaX | elaY | elaZ | --------------------------------------
            fela = @(la) [-sin(la); cos(la); zeros(size(la,1),1)];
            % - nu ----- | nuX | nuY | nuZ | ------------------------------------------
            fnu = @(fi,la) [cos(fi).*cos(la); cos(fi).*sin(la); sin(fi)];

            % compute primary eccentricity
            e2 = 1 - (1 - f).^2;
            % create the Jacobian matrix for each point
            J = cellfun(@(fi,la,h) [fefi(fi,la) .* (fM(fi,a,e2) + h)    fela(la).*(fN(fi,a,e2) + h)    fnu(fi,la)],...
                flh(:,1),flh(:,2),flh(:,3),'UniformOutput',0);
            % compute the covariance matrix in geocentric cartesian
            % coordinates system
            Cxx = cellfun(@(Cff,J) J * Cff * J', Cff, J, 'UniformOutput',0);
        end
        
        % compute parameters of rototranslation between 2D reference systems
        function [Xo, Yo, theta] = inverse2Drt(xp,yp,xq,yq,Xp,Yp,Xq,Yq)
            % The function computes the parameters of a planar roto-traslation (Xo, Yo,
            % theta) given 2 points (P, Q) known in the two systems, starting form the
            % following equations:
            %          Xp = Xo + xp * cos(theta) - yp * sin(theta);
            %          Yp = Yo + xp * sin(theta) + yp * cos(theta);
            %          Xq = Xo + xq * cos(theta) - yq * sin(theta);
            %          Yq = Yo + xq * sin(theta) + yq * cos(theta);
            %
            % SYNTAX:
            %    [Xo, Yo, theta] = inverse2Drt(xp,yp,xq,yq,Xp,Yp,Xq,Yq)
            %
            % OUTPUT:
            %    Xo, Yo --> are given in [m]
            %    theta  --> is given in [rad]
            %
            % INPUT:
            %    xp,yp,xq,yq,Xp,Yp,Xq,Yq  --> are required to be in [m]
            %
            % REMEMBER: theta is the angle from X axis to x one
            %
     
            % solve for theta using parametric tringonometric fucntion tau =
            % tan(theta/2) (from eq. 1-3)
            a = Xp - Xq + xp - xq;
            b = 2*(yp - yq);
            c = Xp - Xq - xp + xq;
            if b^2 - a * c < 0
                a = Yp - Yq + yp - yq;
                b = 2*(xp - xq);
                c = Yp - Yq - yp + yq;
            end
            tau1 = -(b + sqrt(b^2 - a * c)) / a;
            tau2 = -(b - sqrt(b^2 - a * c)) / a;
            % estimates Xo and Yo from tau1 (from eq. 1-2)
            theta1 = 2*atan(tau1);
            Xo1 = Xp - (xp * cos(theta1) - yp * sin(theta1));
            Yo1 = Yp - (xp * sin(theta1) + yp * cos(theta1));
            % estimates Xo and Yo from tau2 (from eq. 1-2)
            theta2 = 2*atan(tau2);
            Xo2 = Xp - (xp * cos(theta2) - yp * sin(theta2));
            Yo2 = Yp - (xp * sin(theta2) + yp * cos(theta2));
            % compute the value of Yq from the two different solutions (eq. 4)
            Yq1 = Yo1 + xq * sin(theta1) + yq * cos(theta1);
            Yq2 = Yo2 + xq * sin(theta2) + yq * cos(theta2);
            % verify wich of the two solutions verify better the condition imposed by
            % eq. 4 and chose it as final solution
            if abs(Yq - Yq1) > abs(Yq - Yq2)
                theta = theta2;
                Xo = Xo2;
                Yo = Yo2;
            elseif abs(Yq - Yq1) <= abs(Yq - Yq2)
                theta = theta1;
                Xo = Xo1;
                Yo = Yo1;
            end
        end
        
        % function to create a diagonal matrix with some square matrix
        % given in input
        function varout = squareblkdiag(varin)
            % La funzione costruisce una matrice diagonale a blocchi quadrata di
            % dimensione m, a partire da blocchi quadrati di dimensione n salvati in
            % una matrice di dimensione m*n dove sono salvati in sequenza tutti i
            % blocchi
            
            n = size(varin,2); % dimensione blocco
            m = size(varin,1); % dimensione matrice
            varout = zeros(m); % inizializzazione matrice diagonale a blocchi
            
            % allocazione blocchi diagonali
            for i=1:n:m
                varout(i:i+n-1,i:i+n-1) = varin(i:i+n-1,:);
            end
        end
        
        % function to do adjustment of well posed integrated network
        % without contraints
        function [coord_est, Cff, oss_rms, st_list, chi2, t, intreal, extreal, lred, icurr, convOK, sigma02, v, vnorm] = gloAdj(obj, flh, D_obs,...
                Z_obs, A_obs, st_list, Dh_obs, GPS_obs, base_obs, a, f, constr, alfa, beta, GPSw, imax)
            % La funzione esegue la compensazione ai minimi quadrati in un sistema di
            % riferimento ellissoidico senza necessit? di vincoli (sistema
            % ben posto)
            %
            % Necessita in INPUT di:
            % - coordinate approssimate dei punti;             coord_app
            % - distanze misurate;                             D_oss
            % - zenit misurati;                                Z_oss
            % - azimut misurati;                               A_oss
            % - altezze strumentali osservate e beta app.;     st_oss
            % - dislivelli                                     Dh_oss
            % - GPS single point;                              GPS_oss
            % - baseline GPS;                                  base_oss
            % - parametri ellissoide;                          ellissoide
            % - punti noti.                                    p_noti
            %
            % Restituisce in OUTPUT:
            % - coordinate stimate dei punti;
            % - errori delle misure stimati (Cyy);
            % - errori delle coordinate stimati (Cxx).
            % - sqm stimati delle osservazioni con traduzione per unirli (sqm_exp)
            %
            %--------------------------------------------------------------------------
            % Input data (they are matrix):
            % - flh     --> |phi(rad)|lambda(rad)|h(m)|
            % - D_obs   --> |St.|Pt.|Distance|hp|Sigma|St.numb|
            % - Z_obs   --> |St.|Pt.|Zenit|hp|Sigma|Left circ|St.numb|
            % - A_obs   --> |St.|Pt.|Azimut|hp|Sigma|Left circ|St.numb|
            % - st_list --> |St.|hs|sigma hs|Beta|
            % - Dh_obs  --> |Back.Pt|For.Pt|Heigth diff.|Sigma|
            % - GPS_obs --> |Pt.|X|Y|Z|Cxx|Cxy|Cxz|Cyy|Cyz|Czz|
            % - base_obs--> |St.|Pt.|X|Y|Z|Cxx|Cxy|Cxz|Cyy|Cyz|Czz|
            % - a       --> ellips major semiaxis a (m)
            % - f       --> ellips flattening f
            % - constr  --> |Pt.|phi(rad)|lambda(rad)|h(m)|
            % - alfa    --> Significativit? test
            % - beta    --> Potenza del test
            % - GPSw    --> Ripesatura matrice di covarianza GPS
            % - imax    --> Numero massimo di iterazioni
            %
            %--------------------------------------------------------------------------
            % Formato dati di OUTPUT:
            % - coord_est --> fi(rad) | lambda(rad) | h(m)
            % - Cff       --> cell array with covariance matrix of each
            %                 point in each row
            % - oss_rms   --> structure with field D, Z, A, lev, gps, base
            %                 each file contain the estimated rms of the observation in
            %                 the same order input
            % - st_list   --> list of the station of TS observation
            %                 |St.|hs|sigma hs|Beta|sigma_beta|
            % - chi2      --> Global test on LS system (boolean)
            % - t         --> local test on LS system
            %                 it is a structure like oss_rms and each field is boolean
            % - intreal   --> internal reliability
			%                 it is a structure like oss_rms and each field is boolean
            % - lred      --> local redundancy
			%                 it is a structure like oss_rms and each field is boolean
            % - extreal   --> external reliability
			%                 |fi(rad) | lambda(rad) | h(m)|
            % - icurr     --> iteration number
            % - convOK    --> flag for convergence (0 NO, 1 YES)
            
            % DEFINIZIONE FUNZIONI PER IL CALCOLO DI GRANDEZZE RICORRENTI -
            % - Geodetiche --> geocentriche ----- | X | Y | Z | -----------------------
            fX = @(fi,la,h,N,e2) [(N+h).*cos(fi).*cos(la), (N+h).*cos(fi).*sin(la), (N*(1-e2)+h).*sin(fi)];
            % - efi ----- | efiX | efiY | efiZ | --------------------------------------
            fefi = @(fi,la) [-sin(fi).*cos(la), -sin(fi).*sin(la), cos(fi)];
            % - ela ----- | elaX | elaY | elaZ | --------------------------------------
            fela = @(la) [-sin(la), cos(la), zeros(size(la,1),1)];
            % - nu ----- | nuX | nuY | nuZ | ------------------------------------------
            fnu = @(fi,la) [cos(fi).*cos(la), cos(fi).*sin(la), sin(fi)];
            % - Dij -------------------------------------------------------------------
            fDij = @(Xi,Xj) sqrt(sum((Xj-Xi).^2,2));
            % - Zij -------------------------------------------------------------------
            fZij = @(eij,nui) acos(sum(nui.*eij,2));
            % - eij ----- |eijX; eijY; eijZ| ------------------------------------------
            feij = @(Xi,Xj) (Xj-Xi)./repmat(sqrt(dot((Xj-Xi),(Xj-Xi), 2)),1,3);
            % - Grannormale -----------------------------------------------------------
            fN = @(fi,a,e2) a./(sqrt(1-e2*(sin(fi).^2)));
            % - Raggio meridiano M-----------------------------------------------------
            fM = @(fi,a,e2) (a.*(1-e2))./(1-e2*sin(fi).^2).^(3/2);
            h = waitbar(0,'Starting computation...');
            % INITIALIZE OUTPUT
            coord_est = [];
            Cff = [];
            oss_rms = [];
            chi2= [];
            t= [];
            intreal= [];
            extreal= [];
            lred= [];
            convOK= 0;
            sigma02= [];
            v= [];
            vnorm= [];
            % PARAMETERS DEFINITION -------------------------------------
            % ellipsoid parameters definition
            e2 = 1 - (1-f)^2;
            % extract number of observations
            nv = 0;
            nd = size(D_obs,1);         % distance
            nz = size(Z_obs,1);         % zenit
            na = size(A_obs,1);         % azimuth
            ns = size(st_list,1);       % station number
            nh = size(Dh_obs,1);        % heigth difference
            ng = size(GPS_obs,1);       % GPS single point
            nb = size(base_obs,1);      % baseline GPS
            npt = size(flh,1);           % total number of points
            no = nd + nz + na + nh + ng .* 3 + nb .* 3 + ns;     % total number of observations
            ni = 3 * npt + 2 * ns;                                % total number of unknown
            
            % LEAST SQARE TERMS INITIALIZATION --------------------------
            % creazione vettore delle osservazioni
            yo = [D_obs(:,3);                                         % Distance
                Z_obs(:,3);                                           % Zenith
                A_obs(:,3);                                           % Azimut
                Dh_obs(:,3);                                          % Delta heigth
                reshape(GPS_obs(:,2:4)',numel(GPS_obs(:,2:4)'),1);    % GPS
                reshape(base_obs(:,3:5)',numel(base_obs(:,3:5)'),1);  % Baseline
                st_list(:,2)];                                        % Instrumental heigth
            % baseline and single point observations are reorder to be X, Y, Z of the
            % same point into consequently rows
            % approximated orientation angle of azimutal zero of total station obs (beta)
            beta_app = st_list(:,4);
            % approximated station heigth
            hs_app = st_list(:,2);
            % GPS covariance matrix
            % they are reshaped to obtain a matrix with 3 column and 3*n_obs rows. In
            % this matrix there are the covariaance matrix 3*3 of the observations one
            % over the other
            Q_GPS = GPS_obs(:,[5 6 7 6 8 9 7 9 10]);
            Q_GPS = obj.squareblkdiag(reshape(Q_GPS',3,3*size(Q_GPS,1))');
            Q_bas = base_obs(:,[6 7 8 7 9 10 8 10 11]);
            Q_bas = obj.squareblkdiag(reshape(Q_bas',3,3*size(Q_bas,1))');
            % Classical topography covariance matrix
            Q_D = diag(D_obs(:,6).^2);
            Q_Z = diag(Z_obs(:,6).^2);
            Q_A = diag(A_obs(:,6).^2);
            Q_Dh = diag(Dh_obs(:,4).^2);
            % Station heigth covariance matrix
            Q_hs = diag(st_list(:,3).^2);
            % Compose the global covariance matrix
            Q = blkdiag(Q_D,Q_Z,Q_A,Q_Dh,GPSw.*Q_GPS,GPSw.*Q_bas,Q_hs);
            Qinv = inv(Q);
            % LEAST SQUARE ESTIMATION ------------------------------------
            % imax = 20;       % max number of iteration
            icurr = 0;        % current iteration
            dx = ones(ni,1); % dx allocation
            % inizializzazione termini per la verifica della convergenza
            fl_prec = 1;    % <1e-12 (<1e-4 m)
            h_prec = 1;     % <1e-4 m
            hs_prec = 1;    % <1e-4 m
            beta_prec = 1;  % <1e-8 (<1e-5 gon)
            while (fl_prec>1e-12 || max(h_prec,hs_prec)>1e-4 || beta_prec >1e-8) && icurr < imax
                icurr = icurr + 1;  % increase iteration number of 1
                waitbar(1/4+1/2/imax*icurr,h,sprintf('Iteration %d',icurr));
                A = zeros(no,ni);   % allocate space for A matrix (with zeros in this way all the terms are initialized)
                b = zeros(no,1);    % allocate space for known term vector b
                % ---------- INIZIALIZZAZIONE VERSORI E TERMINI GEOMETRICI ------------
                N = fN(flh(:,1),a,e2);          % Grannormale
                M = fM(flh(:,1),a,e2);          % Raggio meridiano
                efi = fefi(flh(:,1),flh(:,2));  % versore e_fi per ogni punto delle coordinate approssimate
                ela = fela(flh(:,2));           % versore e_la per ogni punto delle coordinate approssimate
                nu  = fnu(flh(:,1),flh(:,2));   % versore nu per ogni punto delle coordinate approssimate
                % ------------------- OSSERVAZIONI DI DISTANZA ------------------------
                p = D_obs(:,1);    % identificazione del punto p (stazione) e del punto q (punto misurato)
                q = D_obs(:,2);
                nst = D_obs(:,7);  % numero di stazione del punto
                hsp = hs_app(nst); % altezza stazione per ciascun punto
                hsq = D_obs(:,5);  % altezza prisma
                Xp = fX(flh(p,1), flh(p,2), flh(p,3)+ hsp, N(p), e2);  % coordinate geocentriche punti P (stazione)
                Xq = fX(flh(q,1), flh(q,2), flh(q,3)+ hsq, N(q), e2);  % coordinate geocentriche punti Q (osservato)
                epq = feij(Xp, Xq); % calcolo versore tra i due punti (riferito al centro degli strumenti)
                % calcolo degli indici di posizione dei valori da inserire nella
                % matrice A
                irow = (1:nd)'; % indice delle righe interessate
                ifip = sub2ind(size(A), irow, p .* 3 - 2); % indici dei dfi del punto P
                ilap = sub2ind(size(A), irow, p .* 3 - 1); % indici dei dla del punto P
                ihp  = sub2ind(size(A), irow, p .* 3 - 0); % indici dei dh del punto P
                ifiq = sub2ind(size(A), irow, q .* 3 - 2); % indici dei dfi del punto Q
                ilaq = sub2ind(size(A), irow, q .* 3 - 1); % indici dei dla del punto Q
                ihq  = sub2ind(size(A), irow, q .* 3 - 0); % indici dei dh del punto Q
                ihs  = sub2ind(size(A), irow, (ni - ns) + nst); % indici dei dhs del punto P
                % matrice disegno A (differenziali)
                A(ifip) = - dot(epq, efi(p,:), 2) .* (M(p) + flh(p,3)+ hsp).* 10 ^ (-6); % dfi_p
                A(ilap) = - dot(epq, ela(p,:), 2) .* cos(flh(p,1)) .* (N(p) + flh(p,3)+ hsp).* 10 ^ (-6); % dla_p
                A(ihp)  = - dot(epq, nu(p,:), 2); % dh_p
                A(ifiq) =   dot(epq, efi(q,:), 2) .* (M(q) + flh(q,3)+ hsq) .* 10 ^ (-6); % dfi_q
                A(ilaq) =   dot(epq, ela(q,:), 2) .* cos(flh(q,1)) .* (N(q) + flh(q,3)+ hsq) .* 10 ^ (-6); % dla_q
                A(ihq)  =   dot(epq, nu(q,:), 2); % dh_q
                A(ihs)  = - dot(epq, nu(p,:), 2); % dhs
                % termine noto b (osservazioni approssimate)
                b(irow,1) = fDij(Xp, Xq);
                % ------------------- OSSERVAZIONI DI ZENITH --------------------------
                p = Z_obs(:,1);    % identificazione del punto p (stazione) e del punto q (punto misurato)
                q = Z_obs(:,2);
                nst = Z_obs(:,8);  % numero di stazione del punto
                hsp = hs_app(nst); % altezza stazione per ciascun punto
                hsq = Z_obs(:,5);  % altezza prisma
                Xp = fX(flh(p,1), flh(p,2), flh(p,3)+ hsp, N(p), e2);  % coordinate geocentriche approssimate punti P (stazione)
                Xq = fX(flh(q,1), flh(q,2), flh(q,3)+ hsq, N(q), e2);  % coordinate geocentriche approssimate punti Q (osservato)
                epq = feij(Xp, Xq); % calcolo versore tra i due punti (riferiti al punto di misura)
                [xi, eta] = obj.geoid_deviation(obj,flh(p,1).*180./pi,flh(p,2).*180./pi); % estrazione xi ed eta, componenti della deviazione della verticale in direzione fi e la
                delta = repmat(xi,1,3) .* efi(p,:) + repmat(eta,1,3) .* ela(p,:); % versore di deviazione della verticale
                np = nu(p,:) + delta; % calcolo del versore n nel punto p (normale al geoide)
                Zpq = fZij(epq,nu(p,:)); % calcolo zenith dalle coordinate approssimate
                Dpq = fDij(Xp, Xq);      % calcolo distanza dalle coordinate approssimate
                zpq = - repmat((1./(sin(Zpq) .* Dpq)),1,3) .* (np - repmat(dot(np, epq, 2),1,3) .* epq); % termine zpq comune in tutte le derivate
                % calcolo indici posizione celle di A da calcolare
                irow = (nd+1:nd+nz)'; % indice delle righe interessate
                ifip = sub2ind(size(A), irow, p .* 3 - 2); % indici dei dfi del punto P
                ilap = sub2ind(size(A), irow, p .* 3 - 1); % indici dei dla del punto P
                ihp  = sub2ind(size(A), irow, p .* 3 - 0); % indici dei dh del punto P
                ifiq = sub2ind(size(A), irow, q .* 3 - 2); % indici dei dfi del punto Q
                ilaq = sub2ind(size(A), irow, q .* 3 - 1); % indici dei dla del punto Q
                ihq  = sub2ind(size(A), irow, q .* 3 - 0); % indici dei dh del punto Q
                ihs  = sub2ind(size(A), irow, (ni - ns) + nst); % indici dei dhs del punto P
                % matrice disegno A
                A(ifip) =  - dot(zpq, efi(p,:), 2) .* (M(p) + flh(p,3) + hsp) .* 10 ^ (-6); % dfi_p
                A(ilap) =  - dot(zpq, ela(p,:), 2) .* cos(flh(p,1)) .* (N(p) + flh(p,3) + hsp) .* 10 ^ (-6); % dla_p
                A(ihp)  =  - dot(zpq, nu(p,:), 2); % dh_p
                A(ifiq) =    dot(zpq, efi(q,:), 2) .* (M(q) + flh(q,3) + hsq) .* 10 ^ (-6); % dfi_q
                A(ilaq) =    dot(zpq, ela(q,:), 2) .* cos(flh(q,1)) .* (N(q) + flh(q,3) + hsq) .* 10 ^ (-6); % dla_q
                A(ihq)  =    dot(zpq, nu(q,:), 2); % dh_q
                A(ihs)  =  - dot(zpq, nu(p,:), 2); % dhs
                % termine noto b (osservazione approssimata - correzione per deviazione
                % della verticale)
                b(irow) = Zpq - dot(delta, epq, 2) ./ sin(Zpq);
                % ----------- OSSERVAZIONI DI DIREZIONE AZIMUTALE --------
                p = A_obs(:,1);    % identificazione del punto p (stazione) e del punto q (punto misurato)
                q = A_obs(:,2);
                nst = A_obs(:,8);
                hsp = hs_app(nst); % altezza stazione per ciascun punto
                hsq = A_obs(:,5);  % altezza prisma
                Xp = fX(flh(p,1), flh(p,2), flh(p,3)+ hsp, N(p), e2);  % coordinate geocentriche approssimate punti P (stazione)
                Xq = fX(flh(q,1), flh(q,2), flh(q,3)+ hsq, N(q), e2);  % coordinate geocentriche approssimate punti Q (osservato)
                epq = feij(Xp, Xq); % calcolo versore tra i due punti (riferiti al punto di misura)
                [xi, eta] = obj.geoid_deviation(obj,flh(p,1).*180./pi,flh(p,2).*180./pi); % estrazione xi ed eta, componenti della deviazione della verticale in direzione fi e la
                delta = repmat(xi,1,3) .* efi(p,:) + repmat(eta,1,3) .* ela(p,:); % versore di deviazione della verticale
                Zpq = fZij(epq,nu(p,:)); % calcolo zenith dalle coordinate approssimate
                Dpq = fDij(Xp, Xq);      % calcolo distanza dalle coordinate approssimate
                rpq = Xq - Xp;           % vettore differenza di posizione
                apq = repmat(1 ./ (Dpq .* sin(Zpq)).^2,1,3) .*  cross(nu(p,:), rpq, 2); % termine apq per il calcolo del differenziale di Apq (cross esegue il prodotto vettoriale per i vettori componenti ciascuna riga delle matrici in input)
                % calcolo degli indici di posizione dei valori da inserire nella
                % matrice A
                irow = (nd+nz+1:nd+nz+na)'; % indice delle righe interessate
                ifip = sub2ind(size(A), irow, p .* 3 - 2); % indici dei dfi del punto P
                ilap = sub2ind(size(A), irow, p .* 3 - 1); % indici dei dla del punto P
                ihp  = sub2ind(size(A), irow, p .* 3 - 0); % indici dei dh del punto P
                ifiq = sub2ind(size(A), irow, q .* 3 - 2); % indici dei dfi del punto Q
                ilaq = sub2ind(size(A), irow, q .* 3 - 1); % indici dei dla del punto Q
                ihq  = sub2ind(size(A), irow, q .* 3 - 0); % indici dei dh del punto Q
                ihs  = sub2ind(size(A), irow, ni - ns + nst); % indice del differenziale di hs (dhs)
                ibeta = sub2ind(size(A), irow, ni - 2 * ns + nst); % indice del differenziale di beta (dbeta)
                % matrice disegno A
                A(ifip) = - dot(apq, efi(p,:),2) .* (M(p) + flh(p,3) + hsp) .* 10 ^ (-6); % dfi_p
                A(ilap) = - dot(apq, ela(p,:),2) .* cos(flh(p,1)) .* (N(p) + flh(p,3) + hsp) .* 10 ^ (-6); % dla_p
                A(ihp)  = - dot(apq, nu(p,:),2); % dh_p
                A(ifiq) =   dot(apq, efi(q,:),2) .* (M(q) + flh(q,3) + hsq) .* 10 ^ (-6); % dfi_q
                A(ilaq) =   dot(apq, ela(q,:),2) .* cos(flh(q,1)) .* (N(q) + flh(q,3) + hsq) .* 10 ^ (-6); % dla_q
                A(ihq)  =   dot(apq, nu(q,:),2); % dh_q
                A(ihs)  = - dot(apq, nu(p,:),2); % dhs;
                A(ibeta) = - 1;
                % termine noto (osservazione approssimata - correzione per la
                % deviazione della verticale)
                tn = atan2(dot(efi(p,:), rpq, 2), dot(ela(p,:), rpq, 2)) - beta_app(nst);       % calcolo del termine noto
                tn(tn < pi) = tn(tn < pi) + 2 .* pi;                                            % correzione del termine noto se l'angolo ? inferiore a - pi
                tn(tn > pi) = tn(tn > pi) - 2 .* pi;                                            % correzione del termine noto se l'angolo ? superiore a pi
                tn = tn + dot(rpq, nu(p,:),2) ./ (Dpq .* sin(Zpq)).^2 .* ...
                    dot(cross(rpq, nu(p,:), 2), delta, 2); % termine noto con applicata correzione per deviazione della verticale
                b(irow) = tn; % assegnazione del termine noto a b
                % -------------------- OSSERVAZIONI DI LIVELLAZIONE -------------------
                p = Dh_obs(:,1);    % identificazione del punto p (stazione) e del punto q (punto misurato)
                q = Dh_obs(:,2);
                % calcolo degli indici di posizione dei valori da inserire nella
                % matrice A
                irow = (nd+nz+na+1:nd+nz+na+nh)'; % indice delle righe interessate
                ihp  = sub2ind(size(A), irow, p .* 3 - 0); % indici dei dh del punto P
                ihq  = sub2ind(size(A), irow, q .* 3 - 0); % indici dei dh del punto Q
                % matrice disegno A
                A(ihp) = - 1; % dh_p
                A(ihq) =   1; % dh_q
                % termine noto b
                b(nd+nz+na+1:nd+nz+na+nh) = flh(q,3) - flh(p,3);
                % ------------------------ OSSERVAZIONI GPS ---------------------------
                p = GPS_obs(:,1);    % identificazione del punto p (stazione) e del punto q (punto misurato)
                Xp = fX(flh(p,1), flh(p,2), flh(p,3), N(p), e2);  % coordinate geocentriche approssimate punti P
                % calcolo degli indici di posizione dei valori da inserire nella
                % matrice A
                irow = (nd+nz+na+nh+1:3:nd+nz+na+3*ng)'; % indice delle righe interessate per l'osservazione X
                ifip = sub2ind(size(A), irow, p .* 3 - 2); % indici dei dfi del punto P per l'osservazione di X
                ilap = sub2ind(size(A), irow, p .* 3 - 1); % indici dei dla del punto P per l'osservazione di X
                ihp  = sub2ind(size(A), irow, p .* 3 - 0); % indici dei dh del punto P per l'osservazione di X
                % per Y e Z baster? aggiungere all'infice ottenuto rispettivamente 1 e 2, perch? sono nella riga sotto
                % matrice disegno A dX
                A(ifip) = efi(p,1) .* (M(p) + flh(p,3)) .* 10 ^ (-6); % dfi_p
                A(ilap) = ela(p,1) .*  cos(flh(p,1)) .* (N(p) + flh(p,3)) .* 10 ^ (-6); % dla_p
                A(ihp)  = nu(p,1) ; % dh_p
                % matrice disegno A dY
                A(ifip+1) = efi(p,2) .* (M(p) + flh(p,3)) .* 10 ^ (-6); % dfi_p
                A(ilap+1) = ela(p,2) .*  cos(flh(p,1)) .* (N(p) + flh(p,3)) .* 10 ^ (-6); % dla_p
                A(ihp+1)  = nu(p,2) ; % dh_p
                % matrice disegno A dZ
                A(ifip+2) = efi(p,3) .* (M(p) + flh(p,3)) .* 10 ^ (-6); % dfi_p
                A(ilap+2) = ela(p,3) .*  cos(flh(p,1)) .* (N(p) + flh(p,3)) .* 10 ^ (-6); % dla_p
                A(ihp+2)  = nu(p,3) ; % dh_p
                % termine noto b
                b(nd+nz+na+nh+1:nd+nz+na+nh+3*ng) = reshape(Xp',numel(Xp'),1);
                % --------------------- OSSERVAZIONI BASELINE -------------------------
                p = base_obs(:,1);    % identificazione del punto p (stazione) e del punto q (punto misurato)
                q = base_obs(:,2);
                Xp = fX(flh(p,1), flh(p,2), flh(p,3), N(p), e2);  % coordinate geocentriche approssimate punti P (osservato)
                Xq = fX(flh(q,1), flh(q,2), flh(q,3), N(q), e2);  % coordinate geocentriche approssimate punti Q (osservato)
                % calcolo degli indici di posizione dei valori da inserire nella
                % matrice A
                irow = (nd+nz+nh+na+3*ng+1:3:nd+nz+na+3*ng+3*nb)'; % indice delle righe interessate per l'osservazione X
                ifip = sub2ind(size(A), irow, p .* 3 - 2); % indici dei dfi del punto P
                ilap = sub2ind(size(A), irow, p .* 3 - 1); % indici dei dla del punto P
                ihp  = sub2ind(size(A), irow, p .* 3 - 0); % indici dei dh del punto P
                ifiq = sub2ind(size(A), irow, q .* 3 - 2); % indici dei dfi del punto Q
                ilaq = sub2ind(size(A), irow, q .* 3 - 1); % indici dei dla del punto Q
                ihq  = sub2ind(size(A), irow, q .* 3 - 0); % indici dei dh del punto Q
                % per Y e Z baster? aggiungere all'infice ottenuto rispettivamente 1 e 2, perch? sono nella riga sotto
                % matrice disegno A dX
                A(ifip) = -efi(p,1) .* (M(p) + flh(p,3)) .* 10 ^ (-6); % dfi_p
                A(ilap) = -ela(p,1) .*  cos(flh(p,1)) .* (N(p) + flh(p,3)) .* 10 ^ (-6); % dla_p
                A(ihp)  = -nu(p,1) ; % dh_p
                A(ifiq) =  efi(q,1) .* (M(q) + flh(q,3)) .* 10 ^ (-6); % dfi_p
                A(ilaq) =  ela(q,1) .*  cos(flh(q,1)) .* (N(q) + flh(q,3)) .* 10 ^ (-6); % dla_p
                A(ihq)  =  nu(q,1) ; % dh_p
                % matrice disegno A dY
                A(ifip+1) = -efi(p,2) .* (M(p) + flh(p,3)) .* 10 ^ (-6); % dfi_p
                A(ilap+1) = -ela(p,2) .*  cos(flh(p,1)) .* (N(p) + flh(p,3)) .* 10 ^ (-6); % dla_p
                A(ihp+1)  = -nu(p,2) ; % dh_p
                A(ifiq+1) = efi(q,2) .* (M(q) + flh(q,3)) .* 10 ^ (-6); % dfi_p
                A(ilaq+1) = ela(q,2) .*  cos(flh(q,1)) .* (N(q) + flh(q,3)) .* 10 ^ (-6); % dla_p
                A(ihq+1)  = nu(q,2) ; % dh_p
                % matrice disegno A dZ
                A(ifip+2) = -efi(p,3) .* (M(p) + flh(p,3)) .* 10 ^ (-6); % dfi_p
                A(ilap+2) = -ela(p,3) .*  cos(flh(p,1)) .* (N(p) + flh(p,3)) .* 10 ^ (-6); % dla_p
                A(ihp+2)  = -nu(p,3) ; % dh_p
                A(ifiq+2) = efi(q,3) .* (M(q) + flh(q,3)) .* 10 ^ (-6); % dfi_p
                A(ilaq+2) = ela(q,3) .*  cos(flh(q,1)) .* (N(q) + flh(q,3)) .* 10 ^ (-6); % dla_p
                A(ihq+2)  = nu(q,3) ; % dh_p
                % termine noto b
                b(nd+nz+na+nh+ng*3+1:nd+nz+na+nh+3*ng+3*nb) = reshape(Xq',numel(Xq'),1) - reshape(Xp',numel(Xp'),1);
                % ---------------------- OSSERVAZIONI DI ALTEZZA STRUMENTALE ----------
                irow = (nd+nz+na+nh+3*ng+3*nb+1:nd+nz+na+nh+3*ng+3*nb+ns)';
                ihs  = sub2ind(size(A), irow, ni - ns + (1:ns)'); % indice del differenziale di hs (dhs)
                A(ihs) = 1;
                b(irow) = hs_app;
                % -------------------- VINCOLI APPROSSIMATI ---------------------------
                % matrice di vincolo
                if ~isempty(constr)
                    % H = zeros(3*size(constr,1),ni);
                    % p = constr(:,1);
                    nv = 4;
                    H = zeros(4,ni);
                    p = constr(1,1);
                    q = constr(2,1);
                    Xp = fX(flh(p,1), flh(p,2), flh(p,3), N(p), e2);  % coordinate geocentriche approssimate punti P
                    Xq = fX(flh(q,1), flh(q,2), flh(q,3), N(q), e2);
                    % punto fisso
                    irow = 1:3:3*length(p);
                    ifip = sub2ind(size(H), irow', p .* 3 - 2); % indici dei dfi del punto P
                    ilap = sub2ind(size(H), irow', p .* 3 - 1); % indici dei dla del punto P
                    ihp  = sub2ind(size(H), irow', p .* 3 - 0); % indici dei dh del punto P
                    % per Y e Z baster? aggiungere all'infice ottenuto rispettivamente 1 e 2, perch? sono nella riga sotto
                    % matrice disegno A dX
                    H(ifip) = efi(p,1) .* (M(p) + flh(p,3)) .* 10 ^ (-6); % dfi_p
                    H(ilap) = ela(p,1) .*  cos(flh(p,1)) .* (N(p) + flh(p,3)) .* 10 ^ (-6); % dla_p
                    H(ihp)  = nu(p,1) ; % dh_p
                    % matrice disegno A dY
                    H(ifip+1) = efi(p,2) .* (M(p) + flh(p,3)) .* 10 ^ (-6); % dfi_p
                    H(ilap+1) = ela(p,2) .*  cos(flh(p,1)) .* (N(p) + flh(p,3)) .* 10 ^ (-6); % dla_p
                    H(ihp+1)  = nu(p,2) ; % dh_p
                    % matrice disegno A dZ
                    H(ifip+2) = efi(p,3) .* (M(p) + flh(p,3)) .* 10 ^ (-6); % dfi_p
                    H(ilap+2) = ela(p,3) .*  cos(flh(p,1)) .* (N(p) + flh(p,3)) .* 10 ^ (-6); % dla_p
                    H(ihp+2)  = nu(p,3) ; % dh_p
%                     H(ifip) = 10^-6;
%                     H(ilap+1) = 10^-6;
%                     H(ihp+2) = 1;
                    % orientamento
                    irow = 4;
                    ifip = sub2ind(size(H), irow', p .* 3 - 2); % indici dei dfi del punto P
                    ilap = sub2ind(size(H), irow', p .* 3 - 1); % indici dei dla del punto P
                    ihp  = sub2ind(size(H), irow', p .* 3 - 0); % indici dei dh del punto P
                    ifiq = sub2ind(size(H), irow', q .* 3 - 2); % indici dei dfi del punto P
                    ilaq = sub2ind(size(H), irow', q .* 3 - 1); % indici dei dla del punto P
                    ihq  = sub2ind(size(H), irow', q .* 3 - 0); % indici dei dh del punto P
                    epq = feij(Xp, Xq); % calcolo versore tra i due punti (riferiti al punto di misura)
                    Zpq = fZij(epq,nu(p,:)); % calcolo zenith dalle coordinate approssimate
                    Dpq = fDij(Xp, Xq);      % calcolo distanza dalle coordinate approssimate
                    rpq = Xq - Xp;           % vettore differenza di posizione
                    apq = repmat(1 ./ (Dpq .* sin(Zpq)).^2,1,3) .*  cross(nu(p,:), rpq, 2); % termine apq per il calcolo del differenziale di Apq (cross esegue il prodotto vettoriale per i vettori componenti ciascuna riga delle matrici in input)
                    % calcolo degli indici di posizione dei valori da inserire nella
                    % matrice disegno H
                    H(ifip) = - dot(apq, efi(p,:),2) .* (M(p) + flh(p,3)) .* 10 ^ (-6); % dfi_p
                    H(ilap) = - dot(apq, ela(p,:),2) .* cos(flh(p,1)) .* (N(p) + flh(p,3)) .* 10 ^ (-6); % dla_p
                    H(ihp)  = - dot(apq, nu(p,:),2); % dh_p
                    H(ifiq) =   dot(apq, efi(q,:),2) .* (M(q) + flh(q,3)) .* 10 ^ (-6); % dfi_q
                    H(ilaq) =   dot(apq, ela(q,:),2) .* cos(flh(q,1)) .* (N(q) + flh(q,3)) .* 10 ^ (-6); % dla_q
                    H(ihq)  =   dot(apq, nu(q,:),2); % dh_q
                    % termine noto b
                    constr_app = reshape(Xp',numel(Xp'),1);
                    constr_o = constr(1,2:4)';
%                     constr_app = flh(p,:)';
                    tn = atan2(dot(efi(p,:), rpq, 2), dot(ela(p,:), rpq, 2));       % calcolo del termine noto
                    tn(tn < pi) = tn(tn < pi) + 2 .* pi;                            % correzione del termine noto se l'angolo ? inferiore a - pi
                    tn(tn > pi) = tn(tn > pi) - 2 .* pi;                            % correzione del termine noto se l'angolo ? superiore a pi
                    constr_app = [constr_app; tn];
                    constr_o = constr_o(:);
                    flhp = obj.cart2geo(constr(1,2:4),a,f);
                    % flhq = obj.cart2geo(constr(1,2:4),a,f);
                    efip = fefi(flhp(1), flhp(2));
                    elap = fela(flhp(2));
                    rpq = constr(2,2:4) - constr(1,2:4);
                    ori_o = atan2(dot(efip, rpq, 2), dot(elap, rpq, 2));
                    ori_o(ori_o < pi) = ori_o(ori_o < pi) + 2 .* pi;                            % correzione del termine noto se l'angolo ? inferiore a - pi
                    ori_o(ori_o > pi) = ori_o(ori_o > pi) - 2 .* pi;                            % correzione del termine noto se l'angolo ? superiore a pi
                    constr_o = [constr_o(:); ori_o];
%                     constr_o = [flhp(:); ori_o];
                    eta0 = constr_o - constr_app;
                else
                    H = 0;
                    eta0 = 0;
                end                
                % ------------- SOLUZIONE DEL SISTEMA AI MINIMI QUADRATI --------------                
                Y = yo - b;     % termine noto
                Ychk = Y(nd + nz + 1:nd + nz + na);             % estrazione termine relativi alle osservazioni di azimuth
                Ychk(Ychk > 1)  = Ychk(Ychk > 1) - 2 .* pi;     % correzione per direzioni azimutali riferite a due quadranti differenti
                Ychk(Ychk < -1) = Ychk(Ychk < -1) + 2 .* pi;
                Y(nd + nz + 1:nd + nz + na) = Ychk;             % importazione della correzione nel vettore dei termini noti
                
                N = A' * Qinv * A;                               %#ok<MINV> % matrice normale
                R = N + 1e10 .* H' * H;                          % matrice normale + vincoli
                if rcond(R) < 1e-17
                    close(h)
                    return
                end
                tn = A' * Qinv * Y + 1e10 .* H' * eta0;        %#ok<MINV>  % termine noto normale
                dx = R \ tn;                                    % stima dei parametri
                
                dflh = reshape(dx(1:ni-2*ns)',3,npt)';          % creazione matrice di dfi, dla, dh stimati per ogni punto
                dflh(:,1:2) = dflh(:,1:2) .* 10 ^ (-6);         % riscalo i termini relativi a fi e lambda scalati per motivi numerici
                dbeta = dx(ni-2*ns+1:ni-ns);                    % creazione vettore con i termini dbeta
                dhs = dx(ni-ns+1:ni);                           % vettore con termini dhs
                
                flh = flh + dflh;                               % stima delle nuove coordinate
                beta_app = beta_app + dbeta;                    % stima dei nuovi angoli di orientamento
                hs_app = hs_app + dhs;                          % stima delle nuove altezze strumentali
                
                % termini per la verifica della convergenza
                fl_prec = max(max(abs(dflh(:,1:2))));
                h_prec = max(abs(dflh(:,3)));
                hs_prec = max(abs(dhs));
                beta_prec = max(abs(dbeta));
            end % termine del ciclo per la compensazione
            
            if icurr == imax && (fl_prec>1e-12 || max(h_prec,hs_prec)>1e-4 || beta_prec >1e-8)
                close(h)
                return
            end
            waitbar(1/4+1/2,h,sprintf('Least Squares Statistics...'));        
            % %%%%%%%%%%%%%%% CALCOLO STATISTICHE MINIMI QUADRATI %%%%%%%%%%%%%%%%%%%%
            Rinv = inv(R);
            y = A * dx + b; % stima delle osservazioni
            v = yo - y;     % vettori degli scarti
            
            % correzione degli scarti relativi agli azimuth
            Ychk = v(nd + nz + 1:nd + nz + na);             % estrazione termine relativi alle osservazioni di azimuth
            Ychk(Ychk > 1)  = Ychk(Ychk > 1) - 2 .* pi;     % correzione per azimuth riferiti a due quadranti differenti
            Ychk(Ychk < -1) = Ychk(Ychk < -1) + 2 .* pi;
            
            % scarti e stima delle statistiche
            v(nd + nz + 1:nd + nz + na) = Ychk;             % importazione della correzione nel vettore degli scarti
            r = (no + nv) - ni;                             % ridondanza (osservazioni - incognite)
            sigma02 = (v' * Qinv * v) ./ r;                 %#ok<MINV> % varianza a posteriori
            Cxx = sigma02 * (Rinv * N * Rinv);              %#ok<MINV> % covarianza stimata a posteriori dei parametri stimati
            Cyy = sigma02 * (A * Rinv * N * Rinv * A');     %#ok<MINV> % covarianza stimata a posteriori delle osservazioni
            Cvv = sigma02 * (Q - A* Rinv * A');             %#ok<MINV> % covarianza stimata a posteriori degli scarti
            Cvv(abs(Cvv)<1e-15) = 0;                        % metto a zero errori di approssimazione
            
            %--------------------------  TEST ----------------------------
            % alfa = 0.05;                                % affidabilit? dei test
            % beta = 0.95;                                % potenza del test
            % ------------------------- TEST GLOBALE ----------------------------------
            waitbar(1/4+1/2+1/4/7*1,h,sprintf('Global test...'));  
            % Ipotesi: non ? presente neanche un outlayer
            chi2_exp = sigma02 * (no + nv - ni);        % chi quadro sperimentale (si considera una sigma2 a priori pari ad 1)
            chi2_alfa = chi2inv(1 - alfa,no + nv -ni);  % chi quadro limite
            chi2 = chi2_exp <= chi2_alfa;               % risultato del test (1 ipotesi accettata, 0 ipotesi rifiutata)
            
            % --------------------------- TEST LOCALE ---------------------------------
            waitbar(1/4+1/2+1/7*2,h,sprintf('Local test...')); 
            % test sugli scarti normalizzati
            t_exp = v ./ sqrt(diag(Cvv));           % valore sperimentale della ditribuzione t
            t_exp(v==0) = 0;
            t_alfa = tinv(1 - alfa/2, no + nv -ni); % valore limite (significativit? alfa)
            t_test = abs(t_exp) <= t_alfa;          % test (1 no outlayer, 0 outlayer)
            
            t.D = t_test(1:nd,1);                                          % Distance
            t.Z = t_test(nd+1:nd + nz,1);                                  % Zenith
            t.A = t_test(nd+nz+1:nd+nz+na,1);                              % Azimuth
            t.lev = t_test(nd+nz+na+1:nd+nz+na+nh,1);                      % levelling
            t.gps = t_test(nd+nz+na+nh+1:nd+nz+na+nh+3*ng,1);              % GPS
            t.base = t_test(nd+nz+na+nh+3*ng+1:nd+nz+na+nh+3*ng+3*nb,1);   % baseline
            % reshape the gps and base element. They become 3 * n_obs elemnt
            t.gps = reshape(t.gps(:)',3,numel(t.gps)/3)';
            t.base = reshape(t.base(:)',3,numel(t.base)/3)';
            
            % ---------------------- SCARTI NORMALIZZATI -------------------------------
            vnorm.D = t_exp(1:nd,1);                                          % Distance
            vnorm.Z = t_exp(nd+1:nd + nz,1);                                  % Zenith
            vnorm.A = t_exp(nd+nz+1:nd+nz+na,1);                              % Azimuth
            vnorm.lev = t_exp(nd+nz+na+1:nd+nz+na+nh,1);                      % levelling
            vnorm.gps = t_exp(nd+nz+na+nh+1:nd+nz+na+nh+3*ng,1);              % GPS
            vnorm.base = t_exp(nd+nz+na+nh+3*ng+1:nd+nz+na+nh+3*ng+3*nb,1);   % baseline
            % reshape the gps and base element. They become 3 * n_obs elemnt
            vnorm.gps = reshape(vnorm.gps(:)',3,numel(vnorm.gps)/3)';
            vnorm.base = reshape(vnorm.base(:)',3,numel(vnorm.base)/3)';
            
            % ------------------- RIDONDANZA LOCALE ----------------------------------
            waitbar(1/4+1/2+1/4/7*3,h,sprintf('Local redoundancy...'));
            P_At = eye(no) - A* Rinv * A' * Qinv; %#ok<MINV>
            pii = diag(P_At);
            % numerical checking
            if ~isempty(pii(pii<0))
                pii(pii<0) = 0;
                if ~isempty(max(abs(pii(pii<0))))
                    if max(abs(pii(pii<0))) < 1e-12
                        pii(pii<0) = 0;
                    else
                        close(h)
                        return
                    end
                end
            end
            rii = sqrt(pii);  % local redoundancy
            lred.D = rii(1:nd,1);                                          % Distance
            lred.Z = rii(nd+1:nd + nz,1);                                  % Zenith
            lred.A = rii(nd+nz+1:nd+nz+na,1);                              % Azimuth
            lred.lev = rii(nd+nz+na+1:nd+nz+na+nh,1);                      % levelling
            lred.gps = rii(nd+nz+na+nh+1:nd+nz+na+nh+3*ng,1);              % GPS
            lred.base = rii(nd+nz+na+nh+3*ng+1:nd+nz+na+nh+3*ng+3*nb,1);   % baseline
            % reshape the gps and base element. They become 3 * n_obs elemnt
            lred.gps = reshape(lred.gps(:)',3,numel(lred.gps)/3)';
            lred.base = reshape(lred.base(:)',3,numel(lred.base)/3)';
			
			% -------------------- AFFIDABILITA' INTERNA ------------------------------
            waitbar(1/4+1/2+1/4/7*4,h,sprintf('Internal reliability...'));
            %             z_alfa = norminv(1 - alfa/2); % valore limite (significativit? alfa)
%             z_beta = norminv((1 - beta)/2); % valore limite (potenza alfa)
% 			ir = (z_alfa - z_beta) .* sqrt(diag(Q)) ./ rii;
            t_alfa = norminv(1 - alfa/2);   % valore limite (significativit? alfa)
            t_beta = norminv((1 - beta)/2); % valore limite (potenza alfa)
			ir = (t_alfa - t_beta) .* sqrt(diag(Cyy)) ./ rii;
            ir(ir>1e1) = Inf;
            intreal.D = ir(1:nd,1);                                          % Distance
            intreal.Z = ir(nd+1:nd + nz,1);                                  % Zenith
            intreal.A = ir(nd+nz+1:nd+nz+na,1);                              % Azimuth
            intreal.lev = ir(nd+nz+na+1:nd+nz+na+nh,1);                      % levelling
            intreal.gps = ir(nd+nz+na+nh+1:nd+nz+na+nh+3*ng,1);              % GPS
            intreal.base = ir(nd+nz+na+nh+3*ng+1:nd+nz+na+nh+3*ng+3*nb,1);   % baseline
            % reshape the gps and base element. They become 3 * n_obs elemnt
            intreal.gps = reshape(intreal.gps(:)',3,numel(intreal.gps)/3)';
            intreal.base = reshape(intreal.base(:)',3,numel(intreal.base)/3)';
            
			% -------------------- AFFIDABILITA' ESTERNA ------------------------------
			waitbar(1/4+1/2+1/4/7*5,h,sprintf('External reliability...'));
            NAQ = (Rinv * A' * Qinv); %#ok<MINV>
            NAQ(abs(NAQ)<1e-13) = 0;
            er = zeros(ni,no);
            rii_lim = 1e-4;
            for i=1:no
                ei = zeros(no,1);
                if abs(rii(i))>rii_lim % do not compute external reliability if redoundacy is 0 (it create numerical instability)
                    ei(i) = 1;
                    er(:,i) = NAQ * ei * ir(i);
                end
            end
            [er] = max(er,[],2);
			extreal = reshape(er(1:3*npt),npt,3);
            extreal(:,1:2) = extreal(:,1:2) .* 1e-6;
            % Map observations with redoundancy = 0 into observations with
            % reliaibility = Inf            
            gpsidx = repmat(GPS_obs(:,1),1,3)';     % name of gps observation (need to be replicate)
            baseidx = repmat(base_obs(:,2),1,3)';
            obsidx = [D_obs(:,2);Z_obs(:,2);A_obs(:,2);Dh_obs(:,2);gpsidx(:); baseidx(:)]; % order namne of observed point
            % create map
            ptidx = obsidx(abs(rii(1:end-ns))<rii_lim);
            ptidx = unique(ptidx);
            % define their external reliability = Inf
            extreal(ptidx,:) = Inf;
            
            % %%%%%%%%%%%%%%%% SCRITTURA VARIABILI DI OUTPUT %%%%%%%%%%%%%%%%%%%%%%%%%
            waitbar(1/4+1/2+1/4/7*6,h,sprintf('Generating output...'));
            % ------------------ COORDINATE STIMATE E COVARIANZE ----------------------
            coord_est = flh;                   % coordinate finali |fi(rad)|la(rad)|h(m)|
            % esportazione delle matrici di covarianza nella variabile coord
            Cff = cell(size(coord_est,1),1);
            for i = 1:size(coord_est,1)
                j = i * 3 - 2;
                Cff{i,1} = [Cxx(j,j)*10^(-12) Cxx(j,j+1)*10^(-12) Cxx(j,j+2)*10^(-6);
                    Cxx(j,j+1)*10^(-12) Cxx(j+1,j+1)*10^(-12) Cxx(j+1,j+2)*10^(-6);
                    Cxx(j,j+2)*10^(-6) Cxx(j+1,j+2)*10^(-6) Cxx(j+2,j+2)];
            end
            % ------------------------- OBSERVATION RMS -------------------------------
            rms_oss(:,1) = diag(Cyy).^ (1/2);               % sqm singole osservazioni
            
            % extract the rms for each kind of observation
            oss_rms.D = rms_oss(1:nd,1);                                    % Distance
            oss_rms.Z = rms_oss(nd+1:nd+nz,1);                              % Zenith
            oss_rms.A = rms_oss(nd+nz+1:nd+nz+na,1);                        % Azimuth
            oss_rms.lev = rms_oss(nd+nz+na+1:nd+nz+na+nh,1);                % levelling
            oss_rms.gps = rms_oss(nd+nz+na+nh+1:nd+nz+na+nh+3*ng,1);        % GPS
            oss_rms.base = rms_oss(nd+nz+na+nh+3*ng+1:nd+nz+na+nh+3*ng+3*nb,1);   % baseline
            % reshape the gps and base element. They become 3 * n_obs elemnt
            oss_rms.gps = reshape(oss_rms.gps(:)',3,numel(oss_rms.gps)/3)';
            oss_rms.base = reshape(oss_rms.base(:)',3,numel(oss_rms.base)/3)';
            
            % ------------------------ STATION LIST -----------------------------------
            % st_list(:,2) = hs_app;                                                % write estimated station output
            st_list(:,3) = beta_app;                                                % write estimated beta angle
            st_list(:,5) = diag(Cxx(ni-ns+1:ni,ni-ns+1:ni)).^(1/2);                 % write  estimated rms of station heigth
            st_list(:,4) = diag(Cxx(ni-2*ns+1:ni-ns,ni-2*ns+1:ni-ns)).^(1/2);       % write the rms of the estimated beta angle
            convOK = 1;
            
            % ------------------------ RESIDUALS --------------------------------------
            v_all = v;
            clear v;
			v.D = v_all(1:nd,1);                                          % Distance
            v.Z = v_all(nd+1:nd + nz,1);                                  % Zenith
            v.A = v_all(nd+nz+1:nd+nz+na,1);                              % Azimuth
            v.lev = v_all(nd+nz+na+1:nd+nz+na+nh,1);                      % levelling
            v.gps = v_all(nd+nz+na+nh+1:nd+nz+na+nh+3*ng,1);              % GPS
            v.base = v_all(nd+nz+na+nh+3*ng+1:nd+nz+na+nh+3*ng+3*nb,1);   % baseline
            % reshape the gps and base element. They become 3 * n_obs elemnt
            v.gps = reshape(v.gps(:)',3,numel(v.gps)/3)';
            v.base = reshape(v.base(:)',3,numel(v.base)/3)';
            waitbar(1/4+1/2+1/4/7*7,h,sprintf('Done!'));
            close(h)
        end
        
        % function to extract geoid deviation in a set of points
        function [xi, eta] = geoid_deviation(obj, fi, la)
            % obj --> a geonetClass object, used to chose the rigth geoid
            %         model and to access it (it is stored inside)
            % fi  --> vector of latitude (deg)
            % la  --> vector of longitude (deg)
            % fi and la need to be of the same length
            % xi, eta --> vector in radians
            gs = obj.SelectedGeoid;
            eta = interp2(obj.geoid.(gs).LA,obj.geoid.(gs).FI,obj.geoid.(gs).eta,la,fi) .* pi ./ 3600 ./ 180;
            xi = interp2(obj.geoid.(gs).LA,obj.geoid.(gs).FI,obj.geoid.(gs).xi,la,fi) .* pi ./ 3600 ./ 180;
        end
        
        function CloseImportMsg(~, ~, ~)
            % hObject    handle to fImportCoord (see GCBO)
            % eventdata  reserved - to be defined in a future version of MATLAB
            % handles    structure with handles and user data (see GUIDATA)
            
            % Hint: delete(hObject) closes the figure
            global geonetGUI
            delete(geonetGUI.oh);
            geonetGUI.unlockImport;
        end
        
        function CloseHomeMsg(~, ~, ~)
            % hObject    handle to fImportCoord (see GCBO)
            % eventdata  reserved - to be defined in a future version of MATLAB
            % handles    structure with handles and user data (see GUIDATA)
            
            % Hint: delete(hObject) closes the figure
            global geonetGUI
            delete(geonetGUI.hh);
            geonetGUI.unlockImport;
        end
    end
end