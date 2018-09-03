<%@ page 
	contentType="text/html; charset=utf-8" 
	language="java" 
	import="org.ecocean.servlet.ServletUtilities,
			org.ecocean.*,
			java.util.Properties, 
			java.io.FileInputStream, 
			java.io.File, 
			java.io.FileNotFoundException,
			java.util.Iterator
			" %>
<%

String context="context0";
context=ServletUtilities.getContext(request);

	//language setup
	String langCode="en";
	if(session.getAttribute("langCode")!=null){langCode=(String)session.getAttribute("langCode");}

	Properties props=new Properties();
	props.load(getClass().getResourceAsStream("/bundles/"+langCode+"/submit.properties"));



%>
<jsp:include page="header.jsp" flush="true" />
<link rel="stylesheet" href="css/createadoption.css">

<div class="container maincontent adoption-page">
	<h1 class="intro-adopt">Adopt a Manta</h1>
	<h2>Support cutting edge manta research.</h2>
	<section class="adoption-details">
		<p>
			Adopt a manta, give it a nickname, and receive updates each time it's spotted! Funds
			raised by manta adoptions are used to offset the costs of maintaining this global library and
			to support new and existing research projects.
		</p>
		<a href="gallery.jsp?adoptableSharks=true"><button type="button" name="make adoption" class="large">Choose a manta<span class="button-icon" aria-hidden="true"></span></button></a>
	</section>

	<!--
	<section id="custom-donation-image">
		<img src="cust/mantamatcher/img/shark-donation-scale.jpeg" alt="donation options" />
	</section>
	-->

	<%--If you dont want to use a custom image for your donation scale, comment out #custom-donation-image above and use the code below for some simple circles with price options  --%>
	 <section>
		<article class="">
			<div class="container">

				<div class="col-xs-12">
					<h3>Adoption Levels</h3>
				</div>

				<div class="col-xs-6 adoptionOptionDiv">
					<div class="adoptionPriceDiv">
						<h4><strong>Yearling</strong></h4>
						<p class="adoptionOptionComment">$3/m</p>
					</div>
					<p>
						Fun for kids and families that want to start multiple adoptions. 
					</p>
				</div>
				<div class="col-xs-6 adoptionOptionDiv">
					<div class="adoptionPriceDiv">
						<h4><strong>Juvenile</strong></h4>
						<p class="adoptionOptionComment">$6/month</p>
					</div>
					<p>
						Great gift for the conservation minded person who loves mantas. 
					</p>
				</div>
				<div class="col-xs-6 adoptionOptionDiv">
					<div class="adoptionPriceDiv">
						<h4><strong>Adult</strong></h4>
						<p class="adoptionOptionComment">$12/month</p>
					</div>
					<p>
						This level of support is great for a family that wants to adopt a manta together. 
					</p>
				</div>
				<div class="col-xs-6 adoptionOptionDiv">
					<div class="adoptionPriceDiv">
						<h4><strong>Group / Company</strong></h4>
						<p class="adoptionOptionComment">$100/month</p>
					</div>
					<p>
						Did you know a group of rays is called a fever?
					</p>
				</div>
			</div>

		</article>
	</section> 
	


	<section class="adopters-featured">
			<h2>Join the Family!</h2>
			<article class="adopter-feature-gallery">
			
				<%
				Shepherd myShepherd=new Shepherd(context);
				myShepherd.beginDBTransaction();
				try{
					if(myShepherd.getNumAdoptions()>0){
					Iterator<Adoption> adoptions=myShepherd.getAllAdoptionsNoQuery();
					int iter=0;
					while((adoptions.hasNext())&&(iter<4)){
					
					Adoption ad=adoptions.next();
					%>
			
					<div class="adopter" style="width: 190px">
						<div class="adopter-header" >
							<p>Whale Shark Adopter</p>
						</div>
						<img src="/<%=CommonConfiguration.getDataDirectoryName(context) %>/adoptions/<%=ad.getID()%>/thumb.jpg" alt="" />
						<div class="adopter-details">
							<p style="text-align: center;"><%=ad.getAdopterName().trim() %>
							<br><em>adopted</em>
							<br><a href="individuals.jsp?number=<%=ad.getMarkedIndividual() %>"><%=ad.getMarkedIndividual() %></a></p>
							<p style="text-align: center;"><em>"<%=ad.getAdopterQuote().trim() %>"</em></p>
						</div>
					</div>
					<%
						iter++;
					}
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				finally{
					myShepherd.rollbackDBTransaction();
					myShepherd.closeDBTransaction();
				}
				%>
			
			</article>
		</section>




</div>
<jsp:include page="footer.jsp" flush="true" />