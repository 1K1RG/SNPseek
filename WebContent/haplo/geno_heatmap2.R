args<-commandArgs(TRUE)

DEBUG = F
#USE_RAW = T
USE_RAW = F

options(error = quote(dump.frames("testdump", TRUE)))

# args[1] win/linux/mac
# args[2] image format (tiff/png/jpeg)
# args[3] datadir
# args[4] pedfile
# args[5] mapfile
# args[6] summaryfilesnp3kvars.plink.map
# args[7] genomic_coord
# args[8] showgenes
# args[9] genefilename
# args[10] res
# args[11] weight
# args[12] k-groups
# args[13] k-height

args[1]="win"
args[2]="jpeg"
args[3]= "E:/My Document/Downloads"
args[4]= "snp3kvars-LOC_OS04G01110-852918175221169071.ped"
args[5]= "snp3kvars-LOC_OS04G01110-852918175221169071.map"
args[6]= "snp3kvars-LOC_OS04G01110-852918175221169071.summary.txt"
args[7]= "F"
args[8]= "T"
args[9]= "snp3kvars-LOC_OS04G01110-852918175221169071.map.gene"
args[10]= "10.0"
args[11]= "0.85"
args[12]= "0"
args[13]= "2"

normalize_distance=TRUE


library(ggplot2)


#beanstalk
legendpath="/var/lib/tomcat8/webapps/ROOT/haplo/legend.png"
plink_root="/IRCstorage/snpseekdata/current/plink/"

#popcolumnfactor=1.05
popcolumnfactor=0.001
#geneendfactor=0.07
#geneendfactor=0.0004
geneendfactor=0.02


#resizeFactor=10
#xlabelsize=rel(0.2)

#resizeFactor=100
#xlabelsize=rel(0.3)
resizeFactor=50
xlabelsize=rel(0.25)



  offsety=0.044
  #offsetx=0.03
offsetx=0
  

geneendfactor=offsetx

# Total distance
dist_local = 0.85
if(!is.na(args[11])) {
 dist_local=as.numeric(args[11]) 
}

#resizeFactor=10
#xlabelsize=rel(0.2)

#resizeFactor=100
#xlabelsize=rel(0.3)
resizeFactor=50
xlabelsize=rel(0.25)
if(!is.na(args[10])) {
 resizeFactor=as.numeric(args[10]) 
}

kgroups=0
if(!is.na(args[12])) {
  kgroups=as.numeric(args[12]) 
} 
#kgroups=6

kheight=0
if(!is.na(args[13])) {
  kheight=as.numeric(args[13]) 
} 
#kgroups=6


print(resizeFactor)
print(dist_local)
print(kgroups)
print(kheight)



library(methods)

with_sample_ids=FALSE
stringasf=F  # error in print(h) or ggsave() if set to F  

#Error in x$`[[`(i) : not compatible with STRSXP
#Calls: ggsave ... f -> [[ -> [[ -> .local -> <Anonymous> -> .External
#Execution halted

#Error in x$`[[`(i) : not compatible with STRSXP
#Calls: print ... f -> [[ -> [[ -> .local -> <Anonymous> -> .External
#Execution halted

#theme_set(theme_bw())
basic_theme1 =  theme(axis.line=element_blank(),
                      axis.text.x=element_blank(),
                      axis.text.y=element_blank(),
                      #axis.text.y="Sample" , # element_blank(),
                      #axis.title.x= element_text("SNP"), #element_blank(),
                      #axis.title.y="Sample", #element_blank(),
                      axis.ticks.x = element_blank(),
                      axis.ticks.y = element_blank(),
                      #legend.position="none",
                      #legend.box.background = element_rect(fill="#cacaca"),
                      legend.box.background = element_rect(fill="white", color="white"),
                      panel.background=element_blank(),
                      panel.border=element_blank(),
                      panel.grid.major=element_blank(),
                      panel.grid.minor=element_blank(),
                      plot.background=element_blank()
)

basic_theme2 =  theme(#axis.line=element_blank(),
                      #axis.text.x=element_blank(),
                      #axis.text.y=element_blank(),
                      #axis.text.y="Sample" , # element_blank(),
                      #axis.title.x= element_text("SNP"), #element_blank(),
                      #axis.title.y="Sample", #element_blank(),
                      axis.ticks.x = element_blank(),
                      axis.ticks.y = element_blank(),
                      #legend.position="none",
                      #legend.box.background = element_rect(fill="#cacaca"),
                      legend.box.background = element_rect(fill="white", color="white"),
                      panel.background=element_blank(),
                      panel.border=element_blank(),
                      panel.grid.major=element_blank(),
                      #panel.grid.minor=element_blank(),
                      plot.background=element_blank()
)
basic_theme = theme_bw() + theme(panel.grid.major=element_blank()
                    )
 

# default linux env
data_root=args[3]
pedfilename=args[4]
mapfilename=args[5]
summaryfilename= args[6]
imageformat=args[2]

GENOMIC_COORD=F
if(!is.na(args[7])) {
  GENOMIC_COORD= eval(parse(text=args[7]))
}

SHOW_GENE=F
if(!is.na(args[8])) {
  SHOW_GENE= eval(parse(text=args[8]))
}

genefilename=args[9]
if(is.na(genefilename)) {
    SHOW_GENE=F
 }


popcolumnfactor=0.0006
#if(GENOMIC_COORD) {
if(F) {
 popcolumnfactor=0.05
}

SHOW_GENOMIC_SNP_LINES=!GENOMIC_COORD && SHOW_GENE

#plink_root = ""  # system will find

if(is.na(args[1]) || args[1]=="win") {
	#windows
	plink_root="E:/plink_win64/"
	pedfilename=paste0(data_root,"/",pedfilename)
	mapfilename=paste0(data_root,"/",mapfilename)
	summaryfilename= paste0(data_root,"/",summaryfilename)
  	genefilename=paste0(data_root,"/",genefilename)

    legendpath="E:/locem_programs/My Documents/MyEclipse 2016 CI/iric_portal_2/WebRoot/haplo/legend.png"

}

if(is.na(args[1]) || args[1]=="pollux") {
	#pollux
	plink_root="/home/lmansueto/plinkdir/"
	legendpath="/usr/share/apache-tomcat-7.0.42/webapps/iric-portal/haplo/legend.png"
} 

outimagefilename=paste0(pedfilename, ".", imageformat)

oldwd=getwd()
setwd(data_root)

data_root
mapfilename
pedfilename
genefilename



read_plink_dist_sq = function(filebase){
  
  D = read.table( paste0( filebase,".gz") )
  ids = read.table(paste0( filebase,".id") , stringsAsFactors = F, header = F)[[1]]  
  rownames(D) = ids
  colnames(D) = ids
  D
}

#if(USE_RAW){
if(T){
#recode ped to 0,1,2
plink_command=paste0(plink_root,"plink  --ped ",shQuote(pedfilename)," --recodeA --out ",shQuote(pedfilename)," --map ", shQuote(mapfilename), " --a2-allele ", shQuote(mapfilename),".ref")
plink_command
ec = system(plink_command)
}



 hamming_wmiss <- function(X, Y, na_code = NULL) {
  if(is.null(na_code)){
    if(is.numeric(X)){
      if(all(X>=0, na.rm = T)){
        na_code = -1
      }
    } else {
      na_code = ".NA"
    }
  }
  
  if ( missing(Y) ) {

    uniqs <- unique(as.vector(X))
    uniqs = uniqs[ !is.na(uniqs)]
    
    X[ is.na(X)] = na_code
    
    if(na_code %in% uniqs){
      warning("NA code is among the values of X !\n")
    } else {
      uniqs = c(uniqs, na_code)
    }
    
    U <- X == uniqs[1]
    H <- t(U) %*% U
    for ( uniq in uniqs[-1] ) {
      U <- X == uniq
      H <- H + t(U) %*% U
    }
  } else {
    uniqs <- union(X, Y)
    uniqs = uniqs[ !is.na(uniqs)]
    X[ is.na(X)] = na_code
    Y[ is.na(Y)] = na_code
    
    if(na_code %in% uniqs){
      warning("NA code is among the values of X or Y !\n")
    } else {
      uniqs = c(uniqs, na_code)
    }
    
    H <- t(X == uniqs[1]) %*% (Y == uniqs[1])
    for ( uniq in uniqs[-1] ) {
      H <- H + t(X == uniq) %*% (Y == uniq)
    }
  }
  nrow(X) - H
}

 # make BED
plink_command=paste0(plink_root,"plink --ped ",shQuote(pedfilename)," --make-bed  --out ",shQuote(paste0(pedfilename,"out"))," --map ", shQuote(mapfilename), " --a2-allele ", shQuote(mapfilename),".ref")
plink_command
ec = system(plink_command)

############### Reading BED
library("MultiPhen")
plink_command=paste0("reading ",pedfilename, "out.bed")
plink_command

Abed <- MultiPhen::read.plink(paste0(pedfilename,"out"))
# A = MApop[,-(1:6)]
#Abed[1:5,1:10]
#dim(A)
#A[1:4,1:5]
 A = 2 - Abed
 AA = A

 #dim(AA)

 ############## Computing distance
 if(nrow(AA) <= 3024){
     # D_gene = dist(AA, method = "eu")  
     BB = AA
     BB[ is.na(BB) ] = 4

     if(DEBUG){
       print("dim(AA)=")
       dimAA=dim(AA)
       print(dimAA)
       message("dim(BB)=")
       dimBB=dim(BB)
       print(dimBB)
     }


     #source("hamming.R")
     D_gene = hamming_wmiss( t(BB))
     D_gene = D_gene / ncol(AA)

     if(DEBUG){
       message("dim(D_gene)=")
       dimDgene=dim(D_gene)
       print(dimDgene)
     }


} else {
	plink_command = paste0(plink_root, "plink --distance 1-ibs allele-ct square gz  --ped ", shQuote(pedfilename), " --map ", shQuote(mapfilename), " --out " , shQuote(pedfilename))
	plink_command

    ec = system(plink_command) # read your $PATH
    #D_gene = read_plink_dist_sq( paste0(distance_root, ".mdist"))
    D_gene = read_plink_dist_sq( paste0(pedfilename, ".mdist"))

    D_gene = as.matrix(D_gene)
}


hamming <- function(X, Y) {
  if ( missing(Y) ) {
    uniqs <- unique(as.vector(X))
    U <- X == uniqs[1]
    H <- t(U) %*% U
    for ( uniq in uniqs[-1] ) {
      U <- X == uniq
      H <- H + t(U) %*% U
    }
  } else {
    uniqs <- union(X, Y)
    H <- t(X == uniqs[1]) %*% (Y == uniqs[1])
    for ( uniq in uniqs[-1] ) {
      H <- H + t(X == uniq) %*% (Y == uniq)
    }
  }
  nrow(X) - H
}


#AA[1:6,1:6]
cat("\n#------------------------------\n")
# 2. Population given


# load population data
MAsummary = read.table( summaryfilename, sep="\t", header=TRUE , stringsAsFactors = stringasf )
SamplePop <- MAsummary[,2:3]
colnames(SamplePop) <- c("ID","POP")
SamplePop[,1] <- gsub(" ","_", SamplePop[,1])




pop = as.integer(as.factor(SamplePop$POP))
names(pop) = SamplePop$ID
D_pop = as.matrix( hamming( t(pop) ) ) 
all( rownames(D_pop) == SamplePop$ID  )


D2 = D_pop[ rownames(D_gene), colnames(D_gene) ] / 2
stopifnot( all(colnames(D_gene) == colnames(D2) ))

if(sum(is.na(D2))>0){
  stop("There are NAs in the distance by population matrix...\n")
}

mean_local= mean(D_gene[D_gene>0])
mean_global = mean(D2[D2>0])  
print("mean local")
print(mean_local)
print("mean global")
print(mean_global)
if(normalize_distance){
  D_gene = D_gene / mean_local
  D2 = D2 / mean_global
}

# Total distance
dist_local = 0.85
#if( "dist_local" %in% names(hap.options)){
#  dist_local = as.numeric( hap.options$dist_local   )
#}

print("dim(dist_local)")
dim(dist_local)
print("dim(D_gene)")
dim(D_gene)
print("dim(D2)")
dim(D2)
Dtot = dist_local * D_gene + (1-dist_local) * D2 



if(sum(is.na(Dtot))>0){
  warning("There are NAs in the total distance matrix...\n")
  # Imputation - put into different group via positing high distance
  Dtot[ is.na(Dtot) ] = 10*max(Dtot, na.rm = T)
}


# Ordering by hclust
hc = hclust(as.dist(Dtot), method = "ward.D2")
ord_samples = hc$order

cat("Sample order : \n")
head(ord_samples,10)
cat("----------------\n")




#library(hashmap)





library(reshape2)
library(stringr)


MAraw <- read.table( paste0(pedfilename, ".raw"), header=TRUE, stringsAsFactors = stringasf )
origcolnames= as.character(colnames(MAraw)[-(1:6)])
MApop <- MAraw[,-(2:6)]
rownames(MApop) = MApop[,1]
MApop = MApop[ rownames(D_gene),]
stopifnot(all(MApop[,1]==rownames(D_gene)))


   
      if(kgroups>0) {
          MAkgroup<-cutree(hc, k=kgroups)
          uniqk=seq(1:kgroups)
        } else if(kheight>0) {
              MAkgroup<-cutree(hc, h=kheight)
        }
  #MApop$kgroup<-MAkgroup


if(USE_RAW){
  # use loaded from .raw, works
  ################# load from recoded ped.raw
  MApop <- read.table( paste0(pedfilename, ".raw"), header=TRUE, stringsAsFactors = stringasf )
  MApop <- MApop[,-(2:6)]

  rownames(MApop) = MApop[,1]
  MApop = MApop[ rownames(D_gene),]
  stopifnot(all(MApop[,1]==rownames(D_gene)))



  MAsorted <- MApop
  #MAsorted$kgroup=MAkgroup
  rownames(MAsorted) = as.character( MAsorted[,1] )
  MAsorted = MAsorted[ rownames(D_gene)[ord_samples], ]
  origcolnames= as.character(colnames(MAsorted)[-1])

  MAsorted[,1]<- str_pad(1:nrow(MAsorted), 4, pad = "0")
  #MAsorted[1:3,1:7]
  
  posList <- colnames(MApop)[-(1:2)]  
  
  cat(" Changing colnames:\n")
  colnames(MAsorted)[-1] = as.character( seq(1, ncol(MAsorted)-1))
  #MAsorted[1:3,1:7]
  
  #MAsorted <- MAsorted[ord_samples, ]
  # error if used
  #Error in Summary.factor(c(390L, 374L, 358L, 126L, 1554L, 332L, 98L, 378L,  :
  #  'max' not meaningful for factors
  #Execution halted


  
} else {
  cat(" # Using BED file.\n")
  # use loaded from read.plink, dont work,  Error 'max' not meaningful for factors
  MAsorted = cbind.data.frame(FID = rownames(AA), AA)
  # Order first! 
  #MAsorted$kgroup=MAkgroup
  MAsorted <- MAsorted[ ord_samples, ]
  MAsorted$FID <- str_pad( seq(1:nrow(MAsorted)) , 4, pad = "0")
  origcolnames= as.character(colnames(MAsorted)[-1])
  
  
  cat("dim(MAsorted)=", paste(dim(MAsorted), collapse ="*"), "\n")
  
  # For a plink.read, the column name is chr_pos, so chr_ needs to be removed:
  posList <- as.integer( gsub(".*_", "", colnames(AA)) ) 
  
  colnames(MAsorted)[-1] = as.character( order(posList))
  
  #colnames(MAsorted)[-1] = gsub(".*_", "", colnames(MAsorted)[-1])


  
  cat("MAsorted: \n")
  
  #print( MAsorted[1:3, 1:8] )
  cat(" .... \n")
  # error if used
  #Error in Summary.factor(c(390L, 374L, 358L, 126L, 1554L, 332L, 98L, 378L,  :
  #  'max' not meaningful for factors
  #Execution halted
  

  
} 




#reorder pop
POPsorted <- SamplePop

# Check that orders of samples are consistent
stopifnot(all( as.character(SamplePop$ID) == as.character(MApop[,1]) ))

#POPsorted[,1] <-  str_pad( order(ord_samples), 4, pad = "0")
#POPsorted <-  POPsorted[ord_samples, ]
#error if used
#Error in Summary.factor(c(390L, 374L, 358L, 126L, 1554L, 332L, 98L, 378L,  :
#  'max' not meaningful for factors
#Execution halted

#POPsorted$kgroup<-MAkgroup
POPsorted <- POPsorted[ ord_samples, ]
colnames(POPsorted) <- c("Y","pop")
POPsorted$Y <- seq(1:nrow(POPsorted)) 
head(POPsorted)

#print("MAsorted orig")
#print(MAsorted[1:5,1:5])

  positions = as.numeric( gsub(".*_", "", gsub( "X", "", origcolnames)))

if(GENOMIC_COORD || SHOW_GENE) {
  #library(Matrix)
  # assume same chr
#print("origcolnames")
#print(origcolnames[1:5])

  
  firstpos=positions[1]
  positionidx=positions-firstpos+1
  
  

  print(positions)
  print(firstpos)
  print(length(positions))
  positions[length(positions)]

  lastpos=positions[length(positions)]
   print(lastpos)

  #resizeFactor=(lastpos-firstpos)/resizeFactor

  if(GENOMIC_COORD) {
      MAgeno=matrix(NA,nrow(MAsorted),  floor((lastpos-firstpos+1)/resizeFactor) +1 )
      print("dim(MAgeno)")
      dimmageno=dim(MAgeno)
      print(dimmageno)
      idxcount=2
      xlabelbreaks=matrix(NA,1,length(origcolnames))
      for(idx in positionidx) {
        magenoidx=floor(idx/resizeFactor)+1
        #if(GENOMIC_COORD) {
          MAgeno[, magenoidx] = MAsorted[,idxcount]
        #} else {
        #  MAgeno[, magenoidx] = MAsorted[,idxcount-1]
        #}
        xlabelbreaks[idxcount-1]=magenoidx
        idxcount=idxcount+1
      }
      #magenoidx=floor(lastpos/resizeFactor)+1
      #MAgeno[, magenoidx] = MAsorted[,idxcount]
      #xlabelbreaks[idxcount-1]=magenoidx
      print("idxcount")
      print(idxcount)
  }

  bppersnp=(lastpos-firstpos)/length(positions)

    if(SHOW_GENE) {

        GenesCoord <- read.table(genefilename, header=T, stringsAsFactors = F, sep="\t" )
        print(head(GenesCoord))
      
        startpointsorig=as.numeric(GenesCoord$start)
        endpointsorig=as.numeric(GenesCoord$end)
        yminpoints=rep(0.1, nrow(GenesCoord))
        ymaxpoints=rep(0.2, nrow(GenesCoord))
        genelabels=as.character(GenesCoord$name)
     
        library(stringi)
        ymaxpoints[stri_length(genelabels)==0]<-0.3
        yminpoints[stri_length(genelabels)==0]<-0

     
        startpoints=startpointsorig
        endpoints=endpointsorig
      
        print("startpoints")
        print(startpoints)
        print("endpoints")
        print(endpoints)


        # Sample data
        plot.data <- data.frame(start.points=startpoints,
                                end.points=endpoints,
                                 # ,text.label=c("Sample A", "Sample B"),
                                 #,stringsAsFactors = F,
                                ymin.points=yminpoints,
                                ymax.points=ymaxpoints,
                                text.label=as.character(GenesCoord$name)
                                 )
      
        plot.data$text.position <- (startpointsorig+endpointsorig)/2


        p <- ggplot(plot.data, aes(show.legend=F ))+  geom_rect(aes(xmin=start.points, xmax=end.points, ymin= ymin.points, ymax=ymax.points), fill="lightskyblue") + 
            #geom_rect(aes(xmin=start.lines, xmax=end.lines, ymin=0.22, ymax=0.28), fill="darkgray") +
            #label=text.label)) + 
          
         #geom_segment(aes(x=start.lines, y=0.25, xend=end.lines, yend=0.25), size=2, color="darkgray") +
          #geom_text(aes(x=text.position, y=0.15, label=text.label)) + #, size=5) +
          #geom_text(aes(x=text.position, y=-0.5, label=text.label)) + #, size=5) +
          #+ geom_line() + geom_segment(aes(x=start.lines, y=0.25, xend=end.lines, yend=0.25), colour="darkgray")

          basic_theme  +  theme( axis.ticks.y = element_blank(), 
                      axis.text.y = element_blank(),
                      #,axis.text.x = element_blank(),
                      #,axis.text.x = element_text(angle=90, hjust=1, vjust=0, size=rel(0.2))
                      #plot.margin = unit(c(0,2.5,0,0.1), "cm")
                      #plot.margin = unit(c(0,2.5,0,0.1), "cm")
                     
                      #plot.margin = unit(c(0,2.5,0,0.1), "cm")
                      plot.margin = unit(c(0,0,0,0.1), "cm")
                     
                      #plot.margin = unit(c(0,3.4,0,0.78), "cm")
                      ) + xlab(NULL) +  ylab("") +
                      #coord_cartesian(xlim = c(firstpos, lastpos+floor((lastpos-firstpos)*(1-popcolumnfactor))))  #xlim(firstpos,lastpos) + 
                      #coord_cartesian(xlim = c(firstpos, lastpos+(lastpos-firstpos)*geneendfactor*bppersnp))  #xlim(firstpos,lastpos) + 
                      #coord_cartesian(xlim = c(firstpos, lastpos+(lastpos-firstpos)*geneendfactor*bppersnp))  #xlim(firstpos,lastpos) + 
                      #coord_cartesian(xlim = c(firstpos-(lastpos-firstpos)*geneendfactor, lastpos +(lastpos-firstpos)*geneendfactor  ))  #xlim(firstpos,lastpos) + 

                      coord_cartesian(xlim = c(firstpos+(lastpos-firstpos)*geneendfactor, lastpos-(lastpos-firstpos)*geneendfactor  ))  #xlim(firstpos,lastpos) + 

 

       label_y=-0.5

       if(SHOW_GENOMIC_SNP_LINES) {
         
          xidxs<-rep(NA,length(positions))
          idxposcnt=0
          #posinc= (lastpos-firstpos)/(length(positions)-1)
          if(!GENOMIC_COORD) {
            posinc= (lastpos-firstpos)/(length(positions)+1) # added 1 for the pop column
            #posinc= (lastpos-firstpos)/(length(positions+2)) # added 1 for the pop column
          } else {
            posinc= (lastpos-firstpos)/(length(positions+1)) # added 2 for the pop column
          }
          for(pos in positions) {
            xidx=firstpos + idxposcnt*posinc
            #xidx=pos
           # print("xidx")
           # print(xidx)
            xidxs[idxposcnt+1]=xidx
            idxposcnt=idxposcnt+1
          }
          print("positions")
          print(positions)
          
          lines.data <- data.frame(x=positions,y=0,xend=xidxs+posinc,yend=-1)
           p= p +  geom_segment(mapping=aes(x=x, y=y, xend=xend, yend=yend),data=lines.data,size=0.2, color="darkgray") 
           p=p+theme(legend.position="none")
          label_y=-0.5
       } else {
        MAsorted=MAgeno
          label_y=-0.3
       }

        p=p+geom_text(aes(x=text.position, y=label_y, label=text.label)) 

        cat("Drawing genes!\n")
        ggsave( paste0(genefilename,".",imageformat), plot=p, device=imageformat, hei=2, wi=8, dpi=600)
    } else {
      MAsorted=MAgeno
    }
}




imgwidth=8
if(GENOMIC_COORD) {
#if(T) {
  imgwidth=floor((lastpos-firstpos)/1000)+1
} else {
  imgwidth=floor(length(positions)/10)+1
}
imgwidth=max(imgwidth,8)
imgwidth=min(imgwidth,24)


if(kgroups>0 || kheight>0) {
       
      #  library(ape)
      library(ggdendro)

      
     dhc <- as.dendrogram(hc) # ,hang=0.1)
    # dhc <- set(dhc , "labels_cex",0.1)
     #dhc <- rotate(dhc)


    print("coloring dendo")
   #  library(dendextend)
   #  leafcolors= c(c("brown","violet","blue","green","yellow", "orange","red"),rainbow(20))
   #  if(kgroups>0) {
   #    dhc = color_labels(dhc, k = kgroups,col=leafcolors)
   #  } else if(kheight>0) {
   #    dhc = color_labels(dhc, h = kheight,col=leafcolors)
   #  } 

    ddata <- dendro_data(dhc) #, type="rectangle")
    print("ggplot dendo")

     hdendo <- ggplot(segment(ddata)) + geom_segment(aes(x=x, y=y, xend=xend, yend=yend)) +
    #  geom_text(aes(x = x, y = y, label = label, angle = -90, hjust = 0),size=0.5,  data= label(ddata)) +
       geom_text(aes(x = x, y = -0.1, label = label, angle = 0, hjust = 0),size=0.3,  data= label(ddata)) +
    #  scale_y_continuous(expand = c(0.3, 0)) +
       xlab("sample") +
       ylab("height") +

    #  coord_cartesian(xlim = c(nrow(MAsorted)*offsety,nrow(MAsorted)*(1-offsety))) +

    #  coord_cartesian(ylim = c(10*offsety,10*(1-offsety))) +
    #  coord_cartesian(ylim = c(nrow(MAsorted)*offsety,nrow(MAsorted)*(1-offsety))) +
       coord_cartesian(xlim = c(nrow(MAsorted)*offsety,nrow(MAsorted)*(1-offsety)), ylim = c(10*offsety,10*(1-offsety))) +
       scale_y_continuous(position = "left") +
    #  coord_cartesian(xlim = c(10*offsety,10*(1-offsety))) +
       coord_flip()  + #to rotate by 90 degree and then add scale_x_reverse()
       scale_y_reverse()

     if(kheight>0) {
       dheight=data.frame(x=0, y=kheight,  xend=nrow(MAsorted), yend=kheight)
       hdendo = hdendo + geom_segment(data=dheight, mapping=aes(x=x, y=y, xend=xend, yend=yend), size=1, color="blue") 
     } 

     

    #dend <- as.dendrogram(hc))
    #dend <- set(dend, "labels_cex", 2)


    # par(cex=0.1)
    # hcdata <- dendro_data(hc)
    # hdendo  <- ggdendrogram(hcdata, rotate=TRUE, size=2) + theme_dendro()


        #ggsave( paste0(pedfilename,'.hctree.',imageformat), plot=ggdendrogram(hc, rotate = TRUE, size = 1, cex=0.4), device=imageformat, hei=30, wi=4, dpi=800)
        #ggsave( paste0(pedfilename,'.hctree.',imageformat), plot(as.phylo(hc), type = "cladogram", cex = 0.5, label.offset = 1),  device=imageformat, hei=30, wi=imgwidth/2, dpi=800)

      ggsave( paste0(pedfilename,'.hctree.',imageformat), plot=hdendo,  device=imageformat, hei=40, wi=4, dpi=800)
      paste0("image written to ", pedfilename,'.hctree.',imageformat)


 

      if(T) {
        MAsummarySorted= MAsummary[,2:3]
        colnames(MAsummarySorted) <- c("ID","POP")
        MAsummarySorted[,1] <- gsub(" ","_", MAsummarySorted[,1])
        MAsummarySorted$kgroup=MAkgroup
        MAsummarySorted <- MAsummarySorted[ ord_samples, ]
        colnames(MAsummarySorted) <- c("Y","pop","kgroup")
        MAsummarySorted$Y <- seq(1:nrow(MAsummarySorted)) 
        MAsorted$kgroup<-MAsummarySorted$kgroup
        POPsorted$kgroup<-MAsummarySorted$kgroup
      }

       if(kgroups>0) {
#          MAsummary$kgroup<-cutree(hc, k=kgroups)
          uniqk=seq(1:kgroups)
        } else if(kheight>0) {
 #             MAsummary$kgroup<-cutree(hc, h=kheight)
              uniqk=unique(MAsummarySorted$kgroup)
              uniqk=uniqk[!is.na(uniqk)]
              print("uniqk")
              print(uniqk)
              kgroups=length(uniqk)
        }
        #rect.hclust(tree, k = NULL, which = NULL, x = NULL, h = NULL,            border = 2, cluster = NULL)
        # SELECT BY GROUP
 
        

        newgroup=c()
        prevgroup=c()
        ik=1
        nrows=nrow(POPsorted)
        for(i in seq(1:nrows)) {
          if(i==nrows) break;
          if( (MAsummarySorted$kgroup[i]-MAsummarySorted$kgroup[i+1]) != 0 ) {
            newgroup[ik]=i
            prevgroup[ik]=MAsummarySorted$kgroup[i]
            ik=ik+1
          }
        }
        print("newgroup")
        print(newgroup)
        print("prevgroup")
        print(prevgroup)
}




#MAsummaryClustered <- MAsummary 
MAsummaryClustered <- cbind(str_pad(ord_samples, 4, pad = "0"),  "orig_order"= seq(1:nrow(MAsummary)),  MAsummary)


if(kgroups>0) {
  MAsummaryClustered$kgroup<-MAkgroup
  colnames(MAsummaryClustered) <- c( "clust_order", "orig_order", "variety", "iris_id", "pop","mismatch", "snps", "kgroup")
} else {
  colnames(MAsummaryClustered) <- c( "clust_order", "orig_order", "variety", "iris_id", "pop","mismatch", "snps")
}
MAsummaryClustered <- MAsummaryClustered[ord_samples,]
MAsummaryClustered$clust_order <- seq(1:nrow(MAsummaryClustered))
write.table(MAsummaryClustered, paste0(summaryfilename,".clustered.txt"), sep="\t", row.names=F)



MAsortedAll=MAsorted
POPsortedAll=POPsorted
for (ki in seq(0:kgroups)) {
      k=ki-1
      #k=ki
      if(k>0) {
           break
           POPsorted=  subset( POPsortedAll, kgroup==k) 
           if(GENOMIC_COORD) {
              MAsorted= MAsorted[POPsorted] 
              MAsorted= MAsorted[!is.na(MAsorted)]
           } else {
                 MAsorted=  subset( MAsortedAll, kgroup==k) 
           }
      }
      if(k==0) {
        MAsorted=MAsortedAll
        POPsorted=POPsortedAll
        if(kgroups>0) {
          POPsorted$kgroup <-  paste0("k",POPsorted$kgroup)
          MAsorted$kgroup<- paste0("k",MAsorted$kgroup)
        }
      } 
      if(k<0) break


      print(paste0("k=",k, ", rows=", nrow(MAsorted),",",nrow(POPsorted)))

       if(!GENOMIC_COORD) {
        #if(T) {
          MAsortedmelt<-MAsorted
          MAsortedmelt$pop<-POPsorted$pop
          MAsortedmelt$kgroup<-POPsorted$kgroup
          MA <- melt(MAsortedmelt, id=c("FID"))  # , na.rm = F)
        } else {
          if(resizeFactor<100) {
            #MA <- melt(cbind(MAsorted,matrix(NA,nrow(MAsorted),2),POPsorted$pop), id=c("FID")  , na.rm = F)
            MA <- melt(cbind(MAsorted,matrix(NA,nrow(MAsorted),2),POPsorted$pop,matrix(NA,nrow(MAsorted),2),POPsorted$kgroup), id=c("FID")  , na.rm = F)
          } else {
            #MA <- melt(cbind(MAsorted,matrix(NA,nrow(MAsorted),1),POPsorted$pop), id=c("FID")  , na.rm = F)
            MA <- melt(cbind(MAsorted,matrix(NA,nrow(MAsorted),1),POPsorted$pop,matrix(NA,nrow(MAsorted),1),POPsorted$kgroup), id=c("FID")  , na.rm = F)
          
          }
          
          #MA <- melt(MAsorted, id=c("FID"))  # , na.rm = F)
        }


        colnames(MA) <- c("IX","Y","NALT")
        head(MA)

        if(USE_RAW){
          MA$Y = as.numeric( gsub("_.*", "", gsub( "X", "", as.character(MA$Y) )))  
        } else {
          MA$Y = as.numeric( gsub( ".*_", "", as.character(MA$Y) ))  
        }

        MA$IX = as.numeric( MA$IX )
        ##MA$NALT = as.character(MA$NALT )

        head(MA)

         M  = max(MA[ !is.na(MA$NALT) ,]$IX )
         m  = min(MA[ !is.na(MA$NALT) ,]$IX )
         

        pop2color =  c("#60e0c0","aquamarine","#336622","#339910","steelblue","#2045c0","steelblue","#2045c0","purple","lightskyblue","orange","gray","#acacfc","#9cbb9c", "darkgray","yellow","red","brown","violet","blue","green","yellow", "orange","red",
          "#FF0000FF", "#FF4D00FF", "#FF9900FF", "#FFE500FF" ,"#CCFF00FF" ,"#80FF00FF", "#33FF00FF" ,"#00FF19FF")
        names(pop2color) = c("ind1A","ind1B","ind2","ind3","trop1","trop2","trop","subtrop","aro","temp","aus","admix","japx","indx", "0","1","2","k1","k2","k3","k4","k5","k6","k7","k8","k9","k10","k11","k12","k13","k14","k15")

        #  h = ggplot(MA[ !is.na(MA$NALT) ,], aes(x=IX / M , y=Y, fill=as.character(NALT) )) 

        print("level=")
        print(levels(MA[ !is.na(MA$NALT) ,]))

          h = ggplot(MA[ !is.na(MA$NALT) ,], aes(y=IX  , x=Y,  show.legend=F, fill = as.character(NALT))) 

          #if(length(posList) <= 200){
          if(F){
            h = h + geom_tile()
            linesize = 20
          } else {
            h = h + geom_raster() #hjust = 0, vjust = 0)  
           linesize = 2
          }
          
          h = h + scale_fill_manual(values=pop2color) # scale_fill_manual(values = c("0"="darkgray","1" = "yellow", "2" =  "red"))   # white is defualt for NA anyway; need to control NA differently
          #h = h + labs( fill="allele")
          h = h + labs( fill="Legend")
          h = h + basic_theme  + theme(plot.margin = unit(c(0,0,0,0), "cm"))

          uniqchr=unique(gsub( "_.*", "", origcolnames)) 
          uniqchr=uniqchr[!is.na(uniqchr)]
          print("uniqchr")
          print(uniqchr)
            h=h + ylab("Sample")  + ylim(-2,nrow(MAsorted)+2)
         
          h = h  + theme( axis.ticks.y = element_blank(), 
                          axis.text.y = element_blank()
                          #,axis.text.x = element_blank(),
                          ,axis.text.x = element_text(angle=90, hjust=1, vjust=0, size=xlabelsize)
                          ) 

         if(length(uniqchr)==1) {
              h=h+xlab(paste0("CHR ", uniqchr, " SNP Position") )
            } else {
              h=h+xlab("SNP Position") 
            }


          #poslabel= gsub("_","-", as.character(colnames(AA)))

         offsety1=nrow(MAsorted)*offsety
          offsety2=nrow(MAsorted)-offsety1
          offsetx1=ncol(MAsorted)*offsetx
          offsetx2=ncol(MAsorted)-offsetx1

        #if(GENOMIC_COORD) {
        if(GENOMIC_COORD) {
          h = h + scale_x_continuous(breaks=xlabelbreaks , #seq(1:length(poslabel)),
              labels=  gsub("_","-",origcolnames))
        } else {
          #poslabel= gsub("_","-", as.character(colnames(AA)))
          h = h + scale_x_continuous(breaks=seq(1:length(origcolnames)),
              labels=  gsub("_","-",origcolnames))
        }
        # h=h+ coord_cartesian(ylim = c(offsety1,offsety2), xlim = c(0,ncol(MAsorted)))  
         h=h+ coord_cartesian(ylim = c(offsety1,offsety2), xlim = c(offsetx1,offsetx2))  
         h=h+theme(legend.position="none")

        ## Subpop coloring near the edge
        cmpercolumn=2.54*imgwidth/ncol(MAsorted);

        if(!GENOMIC_COORD) {
        #if(T) {

         #POPsortedmelt <- melt(POPsortedmelt, id.vars="Y")

         #h = h + geom_point( mapping  = aes(
         #x=c( ncol(MAsorted)*(1+popcolumnfactor), ncol(MAsorted)*(1+2*popcolumnfactor)),
        # y=Y, col=c(pop,kgroups), data=POPsorted , size=3, show.legend = F, shape="-", inherit.aes = F)

         h = h + geom_point( mapping  = aes(
         x= ncol(MAsorted)+  0.25/cmpercolumn ,  # 1/(2*ncol(MAsorted)),
         y=Y, col=pop), data=POPsorted , size=3, show.legend = F, shape="-", inherit.aes = F)

         #h=h+geom_point( aes(size=0))


         h=h+ annotate(geom = "text", x=ncol(MAsorted)+0.25/cmpercolumn, y=nrow(MAsorted)-150, label="subpop", angle=-90, size=2, color="black") 

        if(kgroups>0) {
           h = h + geom_point( mapping  = aes(
           x= ncol(MAsorted)+ 0.5/cmpercolumn , # 3/ncol(MAsorted),
           y=Y, col=kgroup), data=POPsorted , size=3, show.legend = F, shape="-", inherit.aes = F) + annotate(geom = "text", x=ncol(MAsorted)+0.5/cmpercolumn , angle=-90, size=2, y=nrow(MAsorted)-100, label="K-group", color="black")
        }

         #h = h + geom_point( mapping  = aes(
         #x= floor(ncol(MAsorted)*(1+popcolumnfactor)),
         #x= ncol(MAsorted)*(1+2*popcolumnfactor),
         #y=Y, col=kgroup), data=POPsorted , size=3, show.legend = F, shape="-", inherit.aes = F)


         h=h+scale_color_manual(values = pop2color ) # shape22 square
         h=h+ guides(colour = guide_legend(override.aes = list(size=3))) # + theme(legend.key=element_rect(fill=pop2color))
         }

         #h=h+scale_fill_discrete(labels=c("allele 0","allele 1","allele 2","admix","aro","ind1A","ind1B","ind2","ind3","indx","japx","subtrop","temp","trop"))

         #h = h + scale_fill_manual(values = pop2color
         # )   # white is defualt for NA anyway; need to control NA differently
         # h = h + labs( fill="pop")

         if(k==0) {
             
                    hfinal=h
                    h2=h
                    if(F) {
                      library(gridExtra)
                      # create legend
                      ldata=data.frame( ybot=seq(1:12), ytop=seq(2:13), xl=0, xr=1, txt=names(pop2color[1:12]))
                      ldata$txtpos=(seq(1:12)+seq(2:13))/2
                        l <- ggplot(ldata) +  geom_rect(mapping=aes(xmin=xl, xmax=xr, ymin=ybot, ymax=ytop), show.legend = F , fill=pop2color[1:12]) 
                        #+ geom_text(aes(x=2, y=txtpos, label=txt)) 
                        #+ coord_cartesian(ylim = c(-30,30))  
                        h2 <- arrangeGrob(h,l, layout_matrix = cbind(matrix(1,1,9), matrix(2,1,1)))

                    } 

                    library(png)
                    library(grid)
                    image <- png::readPNG(legendpath)
                    #image <- png("legend.png",bg="white")
                    g <- rasterGrob(image, interpolate=TRUE)
                    plotleg <- ggplot() + annotation_custom(g,xmin=-Inf, xmax=Inf, ymin=-Inf, ymax=Inf)

                    library(gridExtra)

                  plotprop=imgwidth+1

                  if(SHOW_GENE) {
                    hfinal <- arrangeGrob(p,h2,plotleg, layout_matrix = cbind( rbind(matrix(1,1,plotprop), matrix(2,17,plotprop)) , matrix(3,18,1)))
                  }  else {
                    hfinal <- arrangeGrob(h2,plotleg, layout_matrix = cbind(matrix(1,1,plotprop) , matrix(2,1,1)))
                  }
                  cat("Go!\n")
                  ggsave( outimagefilename, plot=hfinal, device=imageformat, height=12, width=imgwidth, dpi=800)
                  paste0("image written to ", outimagefilename)

               

         } else {
            #ggsave( paste0(pedfilename,'.k',k,'.',imageformat), plot=h, device=imageformat, hei=nrow(MAsorted)*12/nrow(MAsortedAll), wi=imgwidth, dpi=800)
            ggsave( paste0(pedfilename,'.k',k,'.',imageformat), plot=h, device=imageformat, hei=12, wi=imgwidth, dpi=800)
            paste0("image written to ", pedfilename,'.k',k,'.',imageformat)            
         }

  }

setwd(oldwd)