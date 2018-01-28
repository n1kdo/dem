
Here it is--the initial public release of DEM, my software to analyze 
line-of-sight radio communications.  I initially wrote this for my senior
project at Southern Polytechnic State University.

I always thought that this code could be used for more than getting me the heck
out of college, and when a discussion thread appeared in the repeater owner's
mailing list asking about software to calculate AHAAT, I knew I had to modify
the code to do the AHAAT calculations (about 6 hours work) and release the code
under the GNU Public License.  I do not have time right now to continue to 
enhance this software, I am busy with my family, my job, and my "real" amateur
radio business...  

This software has been tested and found to be functional on Windows 95, Linux,
HP-UX, AIX.  I expect it will work on Macintosh, but I have never tested that.

Please feel free to improve the code in any way you like, and please forward
your changes to me so I can keep a master version up-to-date.  Some things that
I know could be added without excessive difficulty include:
*  "Bullington" method analysis to indicate signal strength at various locations
    (this is "real" propagation analysis; over hills, distances, etc.)
*  Merged display of other map formats to include geographic features other than
   elevations (roads, towns, civic borders, etc.)
*  Conversion to "Swing" and Java-2D...

Please do not call, write, or email me to ask me to code some enhancement for
you.  I will happily offer advice and assistance to anyone who wants to add
functionality to the software, but I'm not going to do it for you.

What you need:
  You need to have the Java Development Kit (JDK) to compile and run the 
rograms, or the Java Runtime Environment (JRE) to run only.  I used versions
1.1.6 and 1.1.7 to develop this with.  You will need to download 1-degree 
(1:250,000) DEM files from the USGS 
(http://edcwww.cr.usgs.gov/doc/edchome/ndcdb/ndcdb.html) for the area you want
to analyze.  You may need to download up to 4 DEM files if your target is near
a 1-degree boundary.  The program can merge these files, and allow you to create
custom files centered on your area of interest.  See the documentation for 
details...

Jeff Otterson
January 10, 1999