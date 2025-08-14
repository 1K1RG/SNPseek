args<-commandArgs(TRUE)

 #args[1]  filename
 #args[2]  height
 #args[3]  phylosize
 #args[4]  format
 #args[5]  dataroot

nwfile=args[1]
kheight=0
kheight=as.numeric(args[2])
phylosize=1
phylosize=as.numeric(args[3])
imageformat='png'
imageformat=args[4]
data_root=args[5]

pedfilename=gsub(".newick","",nwfile)

old_wd=getwd()
setwd(data_root)


start.time <- Sys.time()
library(ape)
        
#usePackage("ggdendro")
library(ggplot2)
library(ggdendro)

mytree <- read.tree(nwfile)
#plot(mytree)

kgroups=0

if(T) {

       
	   hc<-as.hclust(mytree)
	   ord_samples = hc$order
	   npoints=length(ord_samples)
	   dhc <- as.dendrogram(hc) # ,hang=0.1)
       ddata <- dendro_data(dhc) #, type="rectangle")
       print("ggplot dendo")
       labs <- label(ddata)

       cat("length(labs) ", length(labs) ,"\n")
       cat("length(ord_samples) ", length(ord_samples),"\n")

		end.time <- Sys.time()
		cat("phylotree loading : ", end.time-start.time," secs.")
		start.time <- end.time


       #labscolour='black'
       #if(kgroups>0 || kheight>0) {
       if(F) {
          #labs$kgroup <- MAsorted$kgroup
          labscolour=c() 
          ik=1
          for(k in MAsorted$kgroup) {
            labscolour[ik]=k
            ik=ik+1
          }

       } 

      # draw tree

       hdendo <- ggplot(segment(ddata), aes( show.legend=F)) + geom_segment(aes(x=x, y=y, xend=xend, yend=yend)) 
    #   if(kgroups==0  && kheight==0) {
       if(T) {
    #  geom_text(aes(x = x, y = y, label = label, angle = -90, hjust = 0),size=0.5,  data= label(ddata)) +
        if(length(ord_samples)>100) {
            hdendo = hdendo + geom_text(aes(x = x, y = -0.01, label = label, angle = 0, hjust = 0, show.legend=F),size=0.3,  data= labs) 
          } else {
            hdendo = hdendo + geom_text(aes(x = x, y = -0.01, label = label, angle = 0, hjust = 0, show.legend=F),size=0.8,  data= labs) 
          }
       } else {
        #hdendo = hdendo + geom_text(aes(x = x, y = -0.1, label = label, angle = 0, hjust = 0, colour=labscolour, show.legend=F),size=0.3,  data= label(ddata))  +
        hdendo = hdendo + geom_text(aes(x = x, y = -0.1, label = label, angle = 0, hjust = 0, colour= labs$kgroup , show.legend=F),size=0.3,  data=labs)  +
        scale_colour_manual(values=kgroup2color) 
       }
       #scale_colour_manual(values=kgroup2color) +
    #  scale_y_continuous(expand = c(0.3, 0)) +
       hdendo= hdendo + xlab("sample") +
       ylab("height") +

    #  coord_cartesian(xlim = c(nrow(MAsorted)*offsety,nrow(MAsorted)*(1-offsety))) +

    #  coord_cartesian(ylim = c(10*offsety,10*(1-offsety))) +
    #  coord_cartesian(ylim = c(nrow(MAsorted)*offsety,nrow(MAsorted)*(1-offsety))) +
       #coord_cartesian(xlim = c(nrow(MAsorted)*offsety,nrow(MAsorted)*(1-offsety)), ylim = c(10*offsety,10*(1-offsety))) +
    #   coord_cartesian(xlim = c(nrow(MAsorted)*offsety,nrow(MAsorted)*(1-offsety)), ylim = c(-1,10*(1-offsety))) +
       scale_y_continuous(position = "left") +
    #  coord_cartesian(xlim = c(10*offsety,10*(1-offsety))) +
       coord_flip()  + #to rotate by 90 degree and then add scale_x_reverse()
       scale_y_reverse()

     if(kheight>0) {
       dheight=data.frame(x=0, y=kheight,  xend=npoints, yend=kheight)
       hdendo = hdendo + geom_segment(data=dheight, mapping=aes(x=x, y=y, xend=xend, yend=yend), size=1, color="blue") 
     }  else {
       if(kgroups>0) {
          #hdendo = hdendo +  geom_segment(data=rect.hclust(hc, k = kgroups, border = "red"))
       }
     }


     h_hcl=hdendo
     #if(!is.na(autogroup) && autogroup!='nogroups' ) {
     if(F) {
                    library(gridExtra) 

                    library(grid)
                    image <- png::readPNG(paste0(pedfilename,".",autogroup,".png"))
                    #image <- png("legend.png",bg="white")
                    g <- rasterGrob(image, interpolate=TRUE)
                    plotleg <- ggplot() + annotation_custom(g,xmin=-Inf, xmax=Inf, ymin=-Inf, ymax=Inf)
        h_hcl<-arrangeGrob(plotleg,hdendo,  layout_matrix = rbind( matrix(1,5,1), matrix(2,35,1)))
     }
     

     if(phylosize==1) {
      	ggsave( paste0(pedfilename,'.hctree.',imageformat), plot=h_hcl,  device=imageformat, hei=40, wi=4, dpi=800)
     } else if(phylosize==0) {
      	#ggsave( paste0(pedfilename,'.hctree.',imageformat), plot=h_hcl,  device=imageformat, hei=40, wi=4, dpi=72)
        ggsave( paste0(pedfilename,'.hctree.',imageformat), plot=h_hcl,  device=imageformat, hei=10, wi=2, dpi=300)
     } else if(phylosize==2) {
      if(length(ord_samples)>100) {
      	 ggsave( paste0(pedfilename,'.hctree.',imageformat), plot=h_hcl,  device=imageformat, hei=40, wi=4, dpi=300)
        } else {
          ggsave( paste0(pedfilename,'.hctree.',imageformat), plot=h_hcl,  device=imageformat, hei=12, wi=4, dpi=300)
        }
     }
     paste0("image written to ", pedfilename,'.hctree.',imageformat)
		
		end.time <- Sys.time()
		cat("phylotree drawing : ", end.time-start.time," secs.")
		start.time <- end.time

}


setwd(old_wd)
