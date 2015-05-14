
while(true)
	$read = ConsoleRead()
	if $read <> "" then interpret($read)
	Sleep(100)
WEnd


func interpret($command)
	$command = StringStripCR(StringStripWS($command,3))
	if $command="volumeMute" then
		Send("{VOLUME_MUTE}")
	ElseIf $command="volumeDown" Then
		Send("{VOLUME_DOWN}")
	ElseIf $command="volumeUp" Then
		Send("{VOLUME_UP}")
	ElseIf $command="mediaPlay" Then
		Send("{media_play_pause}")
	ElseIf $command="mediaStop" Then
		Send("{media_stop}")
	ElseIf $command="mediaPrev" Then
		Send("{media_prev}")
	ElseIf $command="mediaNext" Then
		Send("{media_prev}")
	ElseIf $command="ShutDown" Then
		Shutdown(9)
	Else
		$com = StringSplit($command,"^,^",1)
		if $com[1]="sendText" Then
			Send($com[2])
		Else
			MsgBox(0,0,$command)
		EndIf
	EndIf
EndFunc