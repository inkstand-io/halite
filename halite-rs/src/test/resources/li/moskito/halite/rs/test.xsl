<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ibx="http://inbox/model"
	xmlns:hal="http://moskito.li/halite"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	exclude-result-prefixes="xsi ibx hal">
<!--
Template for testing output transformation of message bodies containing a HAL Resource
 -->

<xsl:template match="//hal:resource">
<res>
  	<title>Resource</title>
  	<links>
  		<xsl:apply-templates select="link"/>
  	</links>
</res>
</xsl:template>

<xsl:template match="link">
	<ln relation="{@rel}" hyperreference="{@href}" />
</xsl:template>

</xsl:stylesheet> 