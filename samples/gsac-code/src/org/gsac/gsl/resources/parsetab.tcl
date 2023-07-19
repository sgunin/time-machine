##
##This takes as an argument the IGS rcvr_ant.tab file and extracts out the
##antenna, dome and receiver listing. The table is checked into svn but its at:
##http://igscb.jpl.nasa.gov/igscb/station/general/rcvr_ant.tab
##
##This generates a site.antenna.properties, site.dome.properties and site.receiver.properties file
##For now the properties file just hold the  listing of the values  and do not
##include the descriptions.
##This also generates igsantenna.html, igsdome.html and igsreceiver.html files.
##These should get copied over into ../help
##


proc printHtml {htmlfp var prop desc} {
    puts $htmlfp "<tr valign=top><td width=30%><a name=\"$prop\">$prop</a></td><td>$desc</td></tr>"
}



set fp [open [lindex $argv 0] r]
set c [read $fp]
close $fp

set blobs [list]

set cnt 0
set div {\+----------------------\+}
set div2 {+----------------------+}
set pattern "$div\(.*?)$div\(.*?)$div\(.*)\$"
while {[regexp $pattern $c match header content rest]} {
    if {[regexp {^-+\+\s*$} $header]} {
	set c "$div2$content$div2$rest"
	continue
    }

    lappend blobs [list $header $content]
    set c "$div2$rest"
}

set whats [list dome antenna receiver]
array set fps {}
foreach var  $whats {
    set fps($var) [open site.$var.properties w]
    set fps($var,html) [open igs$var.html w]
    set htmlfp $fps($var,html);
    set url "http://igscb.jpl.nasa.gov/igscb/station/general/rcvr_ant.tab"
    puts $htmlfp "<h2>IGS [string toupper $var]</h2>"
    puts $htmlfp "Generated from the <a href=\"$url\">IGS  rcvr_ant.tab</a> table.<p>"
    puts $htmlfp "<table width=100%>"
}

foreach tuple $blobs {
    foreach {header content} $tuple break;
#    puts "Header: $header"
    set what ""
    if {[regexp -nocase {domes} $header]} {
	set what "dome"
    } elseif {[regexp {Antenna} $header]} {
	set what "antenna"
    } elseif {[regexp {Receivers} $header] || [regexp {Rcvr} $header]} {
	set what "receiver"
    } else {
	continue;
	puts "Huh? $header"
    }
    set fp $fps($what)
    set htmlfp $fps($what,html)
    set desc ""
    set prop ""
    foreach line [split $content "\n"] {
	set line [string trim $line]
	if {$line==""} {continue}
	if {[regexp {^------} $line]} {continue}
	set toks  [split $line |]
	set left [lindex $toks 1]
	set right [lindex $toks 2]
	set left [string trim $left]
	set right [string trim $right]
	if {$left == ""} {
	    append desc $right
	    append desc " "
	} else {
	    if {$prop !=""} {
                ##puts $fp "$prop = $desc"
                ##Just use the ID as the description for now
                #		puts $fp "$prop=$prop"
		puts $fp "$prop"
                printHtml $htmlfp $what $prop $desc 
	    }
	    set prop $left
	    regsub -all -nocase {xxxxxxxxxxxxxxx} $prop {} prop
	    set prop [string trim $prop]
	    set desc $right
	}
    }
#    puts $fp "$prop=$desc"
#    puts $fp "$prop=$prop"
    puts $fp "$prop"
    printHtml $htmlfp $what $prop $desc 

##    puts "$prop = $desc"
}


foreach var  $whats {
    set htmlfp $fps($var,html)
    puts $htmlfp "</table>"
}


foreach var  $whats {
    close $fps($var)
    close $fps($var,html)
}






