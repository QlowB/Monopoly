Option Explicit


Dim fso
set fso = Nothing


'Set fso = WScript.CreateObject("Scripting.FileSystemObject")

Dim terms(7)
terms(0) = ".h"
terms(1) = ".hpp"
terms(2) = ".c"
terms(3) = ".cpp"
terms(4) = ".c++"
terms(5) = ".rc"
terms(6) = ".inl"
terms(7) = ".java"


Dim ordner
Dim lineCount
Dim lineCountMitLeer
Dim characterCount
Dim ausgabe

Set ordner = fso.GetFolder(PathWON())

lineCount = 0
characterCount = 0

Dim oDic
Set oDic = CreateObject("scripting.dictionary")

Dim prot
prot = ""

Set ausgabe = fso.CreateTextFile(PathWON() & "\Protokoll.txt")


Call checkDir(ordner)


Function checkDir(folder)
	Dim subOrdn
	For Each subOrdn In folder.SubFolders
		Call checkDir(subOrdn)
	Next

	Dim file
	For Each file In folder.Files
		If IsCodeFile(file.Path) Then
			
			Dim a
			Dim fLines
			Dim fCharacters
			fLines = 0
			fCharacters = 0
			Set a = fso.OpenTextFile(file.Path)
			Do While Not a.AtEndOfStream
				Dim read
				read = a.ReadLine
				fCharacters = fCharacters + Len(read)
				
				lineCountMitLeer = lineCountMitLeer + 1
				
				If read <> "" Then
					lineCount = lineCount + 1
					fLines = fLines + 1
				End If
			Loop
			
			characterCount = characterCount + fCharacters
			
			'prot = prot & vbCr & "---> " & file.Name & " --- " & fLines
			Dim abstaend
			Dim abstaend2
			abstaend = ""
			abstaend2 = ""
			Dim i
			For i = 0 To (180 - Len(file.path))
				abstaend = abstaend & " "
			Next
	
			For i = 0 To (5 - Len(fLines))
				abstaend2 = abstaend2 & " "
			Next
			ausgabe.WriteLine("---> " & file.path & abstaend & " --- " & fLines & " lines," & abstaend2 & fCharacters & " characters")
		End if
	Next
End Function

prot = "total lines: " & lineCount & vbCr & "total characters: " & characterCount

ausgabe.WriteLine("")
ausgabe.WriteLine("total lines: " & lineCount)
ausgabe.WriteLine("total characters (including spaces): " & lineCountMitLeer)
ausgabe.WriteLine("total characters: " & characterCount)

MsgBox(prot)

Function PathWON()
	PathWON = Left(WScript.ScriptFullName, Len(WScript.ScriptFullName) - Len(WScript.ScriptName))
End Function


Function IsCodeFile(fname)
	Dim term
	For Each term In terms
		If right(fname, Len(term)) = term Then
			IsCodeFile = True
			Exit Function
		End If
	Next
	IsCodeFile = False
End Function



