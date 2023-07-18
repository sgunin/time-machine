function removeEmptySheet(excelFileName, excelFilePath)

sheetName = 'Foglio'; % EN: Sheet, DE: Tabelle, etc. (Lang. dependent)
% Open Excel file.
objExcel = actxserver('Excel.Application');
objExcel.Workbooks.Open(fullfile(excelFilePath, excelFileName)); % Full path is necessary!
% Delete sheets.
try
      % Throws an error if the sheets do not exist.
      objExcel.ActiveWorkbook.Worksheets.Item(3).Delete;
      objExcel.ActiveWorkbook.Worksheets.Item(2).Delete;
      objExcel.ActiveWorkbook.Worksheets.Item(1).Delete;
catch
       % Do nothing.
end
% Save, close and clean up.
objExcel.ActiveWorkbook.Save;
objExcel.ActiveWorkbook.Close;
objExcel.Quit;
objExcel.delete;