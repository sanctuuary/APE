
PostScript=$0'.ps' 
 gmt pscoast -R2.81/50.52/7.84/53.75r -JM6i -P -W0p,white -K > $PostScript
gmt pscoast -R -J -Scornflowerblue -Df -K -P -O >> $PostScript
gmt pscoast -R -J -N1p/thinner -Df -K -O >> $PostScript
gmt pscoast -R -J -Gdarkseagreen2 -N1p/thinner -Df -K -O -P >> $PostScript
gmt psxy -R -J -O -V -Wthinnest $XYZ_table_file >> $PostScript 
 gmt psconvert $PostScript -A -P -Tg
