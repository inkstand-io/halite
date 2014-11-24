<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ibx="http://inbox/model"
	xmlns:hal="http://model.halite.org"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	exclude-result-prefixes="xsi ibx hal">
	
<xsl:include href="common.xsl"/>
<xsl:output method="html" encoding="UTF-8"/>
<!-- 
 
  Template for the HTML document structure
  
 -->
<xsl:template match="//*" priority="0">
<html>
  	<head>
  		<title><xsl:value-of select="name"/></title>
  		<xsl:apply-templates select="." mode="stylesheets"/>
	</head>
	<body>
		<xsl:apply-templates select="." mode="resource">
			<xsl:with-param name="depth" select="1"/>
		</xsl:apply-templates>
 	</body>
</html>
</xsl:template>

<!-- Blank template for stylesheets. Override with higher priority -->
<xsl:template match="*" mode="stylesheets" priority="0">
<!-- intentionally left empty -->
</xsl:template>

<!--

 Template to render all links of the resource as a list.
 
 @param depth
  the depth in the resource structure. used to display html h elements
 
 -->
<xsl:template match="*" mode="resource" priority="0">
	<xsl:param name="depth" select="1"/>
	<xsl:variable name="elementId" select="generate-id(.)"/>
	<div class="reource" id="{$elementId}">		
		<xsl:apply-templates select="self::node()" mode="renderResourceTitle"/>
		<ul class="resource structure">
			<li class="links">
				<xsl:apply-templates select="." mode="renderLinks">
					<xsl:with-param name="elementId" select="$elementId"/>
				</xsl:apply-templates>
			</li>
			<li class="operations">
				<xsl:apply-templates select="." mode="renderOperations">
					<xsl:with-param name="elementId" select="$elementId"/>
				</xsl:apply-templates>
			</li>
			<li class="forms">
				<xsl:apply-templates select="." mode="renderForms">
					<xsl:with-param name="elementId" select="$elementId"/>
				</xsl:apply-templates>
			</li>
			<li class="properties">
				<xsl:apply-templates select="." mode="renderProperties">
					<xsl:with-param name="elementId" select="$elementId"/>
					<xsl:with-param name="depth" select="$depth + 1"/>
				</xsl:apply-templates>
			</li>	
		</ul>
	</div>
</xsl:template>

<!--
 Renders the html h element to display the title of the resource 
 -->
<xsl:template match="*" mode="renderResourceTitle" priority="0">
	<xsl:param name="depth">1</xsl:param>
	<xsl:element name="{concat('h',$depth)}"><xsl:value-of select="local-name()"/> 
		<xsl:if test="@xsi:type">&#160;(<xsl:value-of select="@xsi:type"/>)</xsl:if>
	</xsl:element>	
</xsl:template>

<!--
	Renders a list of available operations on the resource 
	@param elementId
	 the id of the resource whose operations should be rendered
 -->
<xsl:template match="*" mode="renderOperations" priority="0">
	<xsl:param name="elementId"/>
	<div id="{concat('operations',$elementId)}">
	<span class="operations title">Operations</span>
	<ol class="operations">
		<xsl:if test="link[@rel='remove']">
			<li class="delete button">
				<xsl:call-template name="submitButton">
					<xsl:with-param name="elementId" select="$elementId"/>
					<xsl:with-param name="operation">delete</xsl:with-param>
					<xsl:with-param name="name">_method</xsl:with-param>
					<xsl:with-param name="value">delete</xsl:with-param>
				</xsl:call-template>
			</li>
		</xsl:if>
		<xsl:if test="link[@rel='update']">	
			<li class="update button">
				<xsl:call-template name="submitButton">
					<xsl:with-param name="elementId" select="$elementId"/>
					<xsl:with-param name="operation">update</xsl:with-param>
					<xsl:with-param name="name">_method</xsl:with-param>
					<xsl:with-param name="value">put</xsl:with-param>
				</xsl:call-template>
			</li>
		</xsl:if>
		<xsl:for-each select="link[@rel='move']">
			<li class="move button">
			<xsl:call-template name="submitButton">
					<xsl:with-param name="elementId" select="$elementId"/>
					<xsl:with-param name="operation">move</xsl:with-param>
				</xsl:call-template>
			</li>
		</xsl:for-each>
		<xsl:if test="link[@rel='create']">
			<!-- TODO add create document -->
		</xsl:if>
	</ol>
	</div>
</xsl:template>

<!--
	Renders Forms that allow input that is not directly related to properties of the resource(i.e. move, queries) 
	@param elementId
	 the id of the resource whose operations should be rendered
 -->
<xsl:template match="*" mode="renderForms" priority="0">
	<xsl:param name="elementId"/>
	<div id="{concat('forms',$elementId)}">
	<span class="forms title">Forms</span>
	<ol class="forms">
		<xsl:if test="link[@rel='remove']">
			<li class="delete form">
				<xsl:apply-templates select="." mode="deleteForm">
					<xsl:with-param name="elementId" select="$elementId"/>
				</xsl:apply-templates>
			</li>
		</xsl:if>
		<xsl:if test="link[@rel='update']">	
			<li class="update form">
				<xsl:apply-templates select="." mode="updateForm">
					<xsl:with-param name="elementId" select="$elementId"/>
				</xsl:apply-templates>
			</li>
		</xsl:if>
		<xsl:if test="link[@rel='create']">
			<li class="create form">
				<xsl:apply-templates select="." mode="createForm">
					<xsl:with-param name="elementId" select="$elementId"/>
				</xsl:apply-templates>
			</li>
		</xsl:if>
		<xsl:if test="link[@rel='move']">
			<li class="move form">
				<xsl:apply-templates select="." mode="moveForm">
					<xsl:with-param name="elementId" select="$elementId"/>
				</xsl:apply-templates>
			</li>
		</xsl:if>
		
	</ol>
	</div>
</xsl:template>

<!--

 Template to render all links of the resource as a list. If for one relation (rel) multiple links
 are defined, those links are rendered as a sub-list
 
 @param elementId
  the id of the current element whose links should be displayed
  
 -->
<xsl:template match="*" mode="renderLinks" priority="0">
	<xsl:param name="elementId"/>
	<div id="{concat('links',$elementId)}">
		<span class="topic title links">Links</span>
		<ol class="links">
			<xsl:for-each select="link[not(@rel = (preceding-sibling::*/@rel))]">
				<li><xsl:choose>
					<xsl:when test="count(../link[@rel = current()/@rel]) &gt; 1">
						<xsl:apply-templates select="." mode="linkItemClass"/>
						<span class="title"><xsl:value-of select="@rel"/></span>
						<ol class="{@rel}">
							<xsl:for-each select="../link[@rel = current()/@rel]">
								<li><xsl:apply-templates select="." mode="linkItem"/></li>
							</xsl:for-each>
						</ol>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="." mode="linkItemClass"/>
						<xsl:apply-templates select="." mode="linkItem"/>
					</xsl:otherwise>
					</xsl:choose>
				</li>
			</xsl:for-each>
		</ol>
	</div>
</xsl:template>

<!--
 Renders the class attribute for a parent element that contains a link
 -->
<xsl:template match="*" mode="renderProperties" priority="0">
	<xsl:param name="elementId" />
	<xsl:param name="depth" select="1" />
	<div id="{concat('properties',$elementId)}">
		<span class="topic title properties">Properties</span>
		<ol class="properties">
			<xsl:for-each select="*[not(name(.)='link')]|@*">
				<li class="property">
					<xsl:apply-templates select="." mode="renderProperty"/>
				</li>	
			</xsl:for-each>
		</ol>
	</div>
</xsl:template>
<!-- 
 Renders a single property
	@param depth
	 the depth in the resource structure. is used to render the html h elements 
 -->
<xsl:template match="*" mode="renderProperty" priority="0">
	<xsl:param name="depth" select="1" />
	<xsl:choose>
		<xsl:when test="*">
			<xsl:apply-templates select="self::node()" mode="resource">
				<xsl:with-param name="depth" select="$depth" />
			</xsl:apply-templates>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="local-name(.)" />
			:&#160;
			<xsl:value-of select="." />
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- 
	Renders a single property input field for the property update form 
-->
<xsl:template match="*" mode="renderPropertyInputField" priority="0">
	<label><xsl:value-of select="local-name(.)" />:&#160;
			<input type="text" name="{local-name(.)}" value="{.}" />
	</label>
</xsl:template>
	
<xsl:template match="*" mode="linkItemClass">
	<xsl:attribute name="class">
		link 
		<xsl:value-of select="@rel"/>
		<xsl:if test="@name">
			named<xsl:value-of select="@name"/>
		</xsl:if>
		<xsl:if test="@type">
			type <xsl:value-of select="@type"/>
		</xsl:if>
	</xsl:attribute>	
</xsl:template>
<!--
 Renders an html anchor (a) element for the current link  
 -->
<xsl:template match="*" mode="linkItem">
	<a><xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute>
		<xsl:choose>
			<xsl:when test="@title"><xsl:value-of select="@title"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="@rel"/>
				<xsl:if test="@name">/<xsl:value-of select="@name"/></xsl:if>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="@type">&#160;(type:<xsl:value-of select="@type"/>)</xsl:if>	
	</a>
</xsl:template>



<!--
	Renders a submit button that submits a form for the specified operation and element
	@param elementId
		the id of the resource that contains the form to submit
	@param operation
		the operation the form represents. The id of the form is determined by operation + elementId
 -->
<xsl:template name="submitButton">
	<xsl:param name="elementId"/>
	<xsl:param name="operation"/>
	<button type="submit" onclick="document.getElementById('{concat($operation,'Form', $elementId)}').submit()" 
		class="{concat('operation ', $operation)}">
		<xsl:call-template name="ucfirst">
			<xsl:with-param name="value" select="$operation"/>
		</xsl:call-template>
	</button>
</xsl:template>

<!--

 Creates a move form for the current link 
 @param elementId
		the id of the resource 
 -->
<xsl:template match="*" mode="moveForm" priority="0">	
	<xsl:param name="elementId"/>
	<form method="POST" action="{link[@rel='move']/@href}" id="{concat('moveForm',$elementId)}" class="move">
		<span class="form title move">Move</span>
		<label><xsl:value-of select="link/@name"/> source ID:<input name="sourceId" type="text"/></label>
		<input type="hidden" name="_method" value="put"/>
	</form>
</xsl:template>

<!-- 

  Creates a delete form for the current link 
  
  @param elementId
   the id of the current resource
  --> 
<xsl:template match="*" mode="deleteForm" priority="0">
	<xsl:param name="elementId"/>
	<form method="POST" action="{link[@rel='remove']/@href}" id="{concat('deleteForm',$elementId)}" class="delete" >
		<span class="form title delete">Delete</span>
		<input type="hidden" name="_method" value="delete"/>
	</form>
</xsl:template>

<!--  
  
  Creates a update button for the current link 
  
  @depth 
   the depth in the resource hierarchy. is used to display the proper heading
  @param elementId
   the id of the curren resource
  
 -->
<xsl:template match="*" mode="updateForm" priority="0">
	<xsl:param name="depth" select="1"/>
	<xsl:param name="elementId"/>
	<form method="POST" action="{link[@rel='update']/@href}" id="{concat('updateForm',$elementId)}" class="update">
		<input type="hidden" name="_method" value="put"/>
		<span class="form title update">Update</span>
		<ol class="properties">
			<xsl:for-each select="*[not(name(.)='link')]|@*">
				<xsl:if test="not(*)">
					<li class="property">
						<xsl:apply-templates select="." mode="renderPropertyInputField"/>
					</li>
				</xsl:if>				
			</xsl:for-each>
		</ol>
	</form>
</xsl:template>



<!--  
  
  Creates a update button for the current link 
  
  @depth 
   the depth in the resource hierarchy. is used to display the proper heading
  @param elementId
   the id of the curren resource
  
 -->
<xsl:template match="*" mode="createForm" priority="0">
	<xsl:param name="depth" select="1"/>
	<xsl:param name="elementId"/>
	<form method="POST" action="{link[@rel='add']/@href}" id="{concat('createForm',$elementId)}" class="update">
		<span class="form title create">Add</span>
		<!-- empty -->
	</form>
</xsl:template>


</xsl:stylesheet> 