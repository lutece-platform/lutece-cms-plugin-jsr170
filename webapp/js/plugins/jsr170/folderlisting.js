function changeImage(filename, widthmax, heightmax)
{

var im1 = new Image();
var hauteur=0;
var largeur=0;
var tempheight=0;
var tempwidth=0;

im1.src=filename;
largeur=im1.width;
hauteur=im1.height;
if( largeur > widthmax )
      {
           im1.width=widthmax;
           mainimage.width=widthmax;
           mainimage.height=(widthmax*hauteur)/(largeur);

           if( mainimage.height > heightmax)
               {
                   tempheight=mainimage.height;
                   im1.height=heightmax;
                   mainimage.height=heightmax;
                   mainimage.width=(heightmax*widthmax)/(tempheight);
                   document.mainimage.src=im1.src;
               }
           else
               {
                   document.mainimage.src=im1.src;
               }
      }
else
      {
           if( hauteur > heightmax )
              {
                  im1.height=heightmax;
                  mainimage.height=heightmax;
                  mainimage.width=(heightmax*largeur)/(hauteur);
                  document.mainimage.src=im1.src;
              }
           else
              {
                  mainimage.width=largeur;
                  mainimage.height=hauteur;
                  document.mainimage.src=im1.src;
              }
      }

};