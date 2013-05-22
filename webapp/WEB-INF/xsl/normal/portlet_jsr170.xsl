<?xml version="1.0" encoding="ISO-8859-15"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:param name="site-path" select="site-path" />

<xsl:template match="portlet">
    
	<div class="portlet" id="portlet_id_{portlet-id}" name="portlet_id_{portlet-id}" >
		<fieldset>
        <xsl:if test="not(string(display-portlet-title)='1')">
			<legend>
				<xsl:value-of disable-output-escaping="yes" select="portlet-name" />
			</legend>
        </xsl:if>
		<div class="portlet-content">
		    <xsl:apply-templates select="jsr170-portlet-error" />
		    <xsl:apply-templates select="jsr170-portlet" />
		    <xsl:apply-templates select="jsr170-portlet-modify" />
		    <xsl:apply-templates select="jsr170-portlet-history" />
		</div>
		</fieldset>
	</div>
</xsl:template>


<xsl:template match="jsr170-portlet">
	<xsl:apply-templates select="admin-view-combo" />
	<xsl:apply-templates select="error-upload" />
    <ul class="breadcrumb">
    	<xsl:apply-templates select="breadcrumbs" />
    </ul>
  	<table class="table table-stripped table-condensed">
	    <tr>
            <th></th>
            <th>Nom</th>
            <th>Taille</th>
            <th>Date</th>
            <th>Actions</th>
        </tr>
        <xsl:apply-templates select="directory" />
	    <xsl:apply-templates select="file" />
     </table>

	<xsl:if test="canWrite='true'">
    <form name="form_upload_{../portlet-id}" action="jsp/site/plugins/jsr170/UploadFile.jsp" enctype="multipart/form-data" method="post">
	<input type="hidden" name="portlet_id" value="{../portlet-id}" />
	<input type="hidden" name="directory_path" value="{directory-path}" />
	<input type="hidden" name="view_id_{../portlet-id}" value="{current-view-id}" />
	<input type="hidden" name="page_id" value="{../page-id}" />	
	<label for="upload_file">Ajouter un fichier</label>
	<div class="input-append">
		<input type="file" name="upload_file" />
		<button type="submit" class="btn"><i class="icon-plus">&#160;</i>&#160;Ajouter</button>
		<!-- input type="submit" class="btn" value="Ajouter" / -->
	</div>
	</form>
    <form name="form_mkdir_{../portlet-id}" action="jsp/site/plugins/jsr170/CreateDirectory.jsp" method="post">
	<input type="hidden" name="portlet_id" value="{../portlet-id}" />
	<input type="hidden" name="directory_path" value="{directory-path}" />
	<input type="hidden" name="view_id_{../portlet-id}" value="{current-view-id}" />
	<input type="hidden" name="page_id" value="{../page-id}" />	
	<label for="new_directory">Créer un répertoire</label>
	<div class="input-append">
		<input type="text" name="new_directory" />
		<button type="submit" class="btn"><i class="icon-plus">&#160;</i>&#160;</button>
	</div>
    </form>
    </xsl:if>

</xsl:template>

<xsl:template match="jsr170-portlet-modify">
    <form name="form_upload_{../portlet-id}" action="jsp/site/plugins/jsr170/ModifyFile.jsp" enctype="multipart/form-data" method="post">
	<input type="hidden" name="portlet_id" value="{../portlet-id}" />
	<input type="hidden" name="view_id_{../portlet-id}" value="{current-view-id}" />
	<input type="hidden" name="page_id" value="{../page-id}" />	
	<input type="hidden" name="file_id" value="{file/file-id}" />
	<label for="upload_file">Modifier le fichier <xsl:value-of select="file/file-name" /></label>
	<input type="file" class="input-xlarge" name="upload_file" />
	<div class="form-actions">
		<button type="submit" class="btn btn-primary">
			<i class="icon-ok icon-white">&#160;</i>&#160;Modifier
		</button>
		<a class="btn" href="{$site-path}?portlet_id={/portlet/portlet-id}&#38;page_id={/portlet/page-id}&#38;view_id_{/portlet/portlet-id}={current-view-id}&#38;file_id_{/portlet/portlet-id}={parent-id}#portlet_id_{/portlet/portlet-id}" >
			<i class="icon-remove-circle">&#160;</i>&#160;<xsl:text>Annuler</xsl:text>
		</a>
	</div>
    </form>
</xsl:template>

<xsl:template match="jsr170-portlet-history">
	<table class="table table-stripped table-condensed">
	    <tr>
            <th></th>
            <th>Version</th>
            <th></th>
            <th>Date</th>
            <th></th>
        </tr>
		   <xsl:apply-templates select="version" />
     </table>
	 <a class="btn" href="{$site-path}?portlet_id={/portlet/portlet-id}&#38;page_id={/portlet/page-id}&#38;view_id_{/portlet/portlet-id}={current-view-id}&#38;file_id_{/portlet/portlet-id}={parent-id}#portlet_id_{/portlet/portlet-id}" >
		<i class="icon-remove-circle">&#160;</i>&#160;<xsl:text>Annuler</xsl:text>
	 </a>
</xsl:template>

<xsl:template match="error-upload">
	<div class="alert"> 
    	<xsl:value-of select="." />
    </div>
</xsl:template>


<xsl:template match="file">
	<tr>
		<td>
			<xsl:choose>
			  	<xsl:when test="(file-extension = 'jpg') or (file-extension = 'JPG')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_image.png" height="16" width="16" border="0"/>
			 	</xsl:when>
		        <xsl:when test="(file-extension = 'gif') or (file-extension = 'GIF')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_image.png" height="16" width="16" border="0"/>
			  	</xsl:when>
		        <xsl:when test="(file-extension = 'png') or (file-extension = 'PNG')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_image.png" height="16" width="16" border="0"/>
			  	</xsl:when>
		        <xsl:when test="(file-extension = 'bmp') or (file-extension = 'BMP')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_image.png" height="16" width="16" border="0"/>
				</xsl:when>
		        <xsl:when test="(file-extension = 'doc') or (file-extension = 'rtf')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_word.png" height="16" width="16" border="0"/>
			  	</xsl:when>
		        <xsl:when test="(file-extension = 'xls') or (file-extension = 'csv')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_excel.png" height="16" width="16" border="0"/>
			  	</xsl:when>
		        <xsl:when test="(file-extension = 'ppt')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_ppt.png" height="16" width="16" border="0"/>
			  	</xsl:when>
			    <xsl:when test="(file-extension = 'pdf')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_pdf.png" height="16" width="16" border="0"/>
				</xsl:when>
		        <xsl:when test="(file-extension = 'txt') or (file-extension = 'css')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_txt.png" height="16" width="16" border="0"/>
				</xsl:when>
		        <xsl:when test="(file-extension = 'zip') or (file-extension = 'rar')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_zip.png" height="16" width="16" border="0"/>
			  	</xsl:when>
		        <xsl:when test="(file-extension = 'js')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_js.png" height="16" width="16" border="0"/>
			  	</xsl:when>
		        <xsl:when test="(file-extension = 'java') or (file-extension = 'jsp')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_java.png" height="16" width="16" border="0"/>
			  	</xsl:when>
		        <xsl:when test="(file-extension = 'html') or (file-extension = 'htm')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_html.png" height="16" width="16" border="0"/>
			  	</xsl:when>
		        <xsl:when test="(file-extension = 'xml')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_xml.png" height="16" width="16" border="0"/>
			  	</xsl:when>
		        <xsl:when test="(file-extension = 'xsl')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_xsl.png" height="16" width="16" border="0"/>
			  	</xsl:when>
				<xsl:when test="(file-extension = 'ods')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_ods.jpg" height="16" width="16" border="0"/>
			  	</xsl:when>
				<xsl:when test="(file-extension = 'odt')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_odt.jpg" height="16" width="16" border="0"/>
			  	</xsl:when>
				<xsl:when test="(file-extension = 'odp')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_odp.jpg" height="16" width="16" border="0"/>
			  	</xsl:when>
				<xsl:when test="(file-extension = 'sxc')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_sxc.jpg" height="16" width="16" border="0"/>
			  	</xsl:when>
				<xsl:when test="(file-extension = 'sxi')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_sxi.jpg" height="16" width="16" border="0"/>
			  	</xsl:when>
				<xsl:when test="(file-extension = 'sxw')">
				        <img src="images/local/skin/plugins/jsr170/icons/icon_sxw.jpg" height="16" width="16" border="0"/>
			  	</xsl:when>
		        <xsl:otherwise>
        			<img src="images/local/skin/plugins/jsr170/icons/icon_unknown.png" height="16" width="16" border="0"/>
		        </xsl:otherwise>
			</xsl:choose>
		</td>
  		<td>
			<a href="jsp/site/plugins/jsr170/DisplayFile.jsp?portlet_id={../../portlet-id}&#38;view_id_{../../portlet-id}={../current-view-id}&#38;file_id={file-id}&#38;page_id={../../page-id}">
				<xsl:apply-templates select="file-name" />
			</a>
		</td>
		<td><xsl:value-of select="file-size"/>Ko</td>
		<td><xsl:value-of select="file-date"/></td>
		<td>
			<xsl:if test="file-lock='false' or (file-lock='true' and file-owns-lock='true')">
				<xsl:if test="../canRemove='true'">
				<a class="btn btn-mini btn-danger" href="jsp/site/plugins/jsr170/DoDeleteFile.jsp?portlet_id={../../portlet-id}&#38;view_id_{../../portlet-id}={../current-view-id}&#38;file_id={file-id}&#38;page_id={../../page-id}">
					<i class="icon-trash icon-white" alt="supprimer" title="Supprimer">&#160;</i>&#160;
				</a>
				<a class="btn btn-mini" href="{$site-path}?portlet_id={../../portlet-id}&#38;view_id_{../../portlet-id}={../current-view-id}&#38;file_id={file-id}&#38;page_id={../../page-id}&#38;action_{../../portlet-id}=modify">
					<i class="icon-edit" alt="modifier" title="Modifier">&#160;</i>&#160;
				</a>
				</xsl:if>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="file-versionable='true'">
					<a  class="btn btn-mini" href="{$site-path}?portlet_id={../../portlet-id}&#38;view_id_{../../portlet-id}={../current-view-id}&#38;file_id={file-id}&#38;page_id={../../page-id}&#38;action_{../../portlet-id}=history">
						<!-- img src="images/local/skin/plugins/jsr170/actions/action_history.png" alt="Consulter l'historique" title="Consulter l'historique" / -->
						<i class="icon-list" alt="Consulter l'historique" title="Consulter l'historique">&#160;</i>&#160;
					</a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="file-lock='false' or (file-lock='true' and file-owns-lock='true')">
					<a class="btn btn-mini" href="jsp/site/plugins/jsr170/DoAddFileVersioning.jsp?portlet_id={../../portlet-id}&#38;view_id_{../../portlet-id}={../current-view-id}&#38;file_id={file-id}&#38;page_id={../../page-id}">
						<!-- img src="images/local/skin/plugins/jsr170/actions/action_checkout.png" alt="Ajouter au gestionnaire de versions" title="Ajouter au gestionnaire de versions" / -->
						<i class="icon-star" alt="Ajouter au gestionnaire de versions" title="Ajouter au gestionnaire de versions">&#160;</i>&#160;
					</a>
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>

			<xsl:choose>
				<xsl:when test="file-lock='true' and file-owns-lock='true'">
					<a class="btn btn-mini btn-danger" href="jsp/site/plugins/jsr170/DoUnlockFile.jsp?portlet_id={../../portlet-id}&#38;view_id_{../../portlet-id}={../current-view-id}&#38;file_id={file-id}&#38;page_id={../../page-id}&#38;action_{../../portlet-id}=history">
						<!-- img src="images/local/skin/plugins/jsr170/actions/action_lock_open.png" alt="Retirer le verrou" title="Retirer le verrou" / -->
						<i class="icon-remove icon-white" alt="Retirer le verrou" title="Retirer le verrou">&#160;</i>&#160;
					</a>
				</xsl:when>
				<xsl:when test="file-lock='true' and file-owns-lock='false'">
					<a class="btn btn-mini btn-danger" href="jsp/site/plugins/jsr170/DoUnlockFile.jsp?portlet_id={../../portlet-id}&#38;view_id_{../../portlet-id}={../current-view-id}&#38;file_id={file-id}&#38;page_id={../../page-id}&#38;action_{../../portlet-id}=history">
					<!-- img src="images/local/skin/plugins/jsr170/actions/action_lock_delete.png" alt="Fichier verrouillé" title="Fichier verrouillé" / -->
						<i class="icon-trash icon-white" alt="Fichier verrouillé" title="Fichier verrouillé">&#160;</i>&#160;
					</a>
				</xsl:when>
				<xsl:otherwise>
					<a class="btn btn-mini" href="jsp/site/plugins/jsr170/DoLockFile.jsp?portlet_id={../../portlet-id}&#38;view_id_{../../portlet-id}={../current-view-id}&#38;file_id={file-id}&#38;page_id={../../page-id}&#38;action_{../../portlet-id}=history">
						<!-- img src="images/local/skin/plugins/jsr170/actions/action_lock_add.png" alt="Poser le verrou" title="Poser le verrou" / -->
						<i class="icon-lock" alt="Poser le verrou" title="Poser le verrou">&#160;</i>&#160;
					</a>
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</tr>
	<xsl:apply-templates select="file-content" />
</xsl:template>

<xsl:template match="directory">
	<tr>
		<td>
			<img src="images/local/skin/plugins/jsr170/icons/icon_directory.png" class="thumblist-nano"/>  
		</td>
  		<td>
	        <a href="{$site-path}?portlet_id={../../portlet-id}&#38;page_id={../../page-id}&#38;view_id_{../../portlet-id}={../current-view-id}&#38;file_id_{../../portlet-id}={file-id}#portlet_id_{../../portlet-id}" target="_top" >
		        <xsl:apply-templates select="directory-name" />
			</a>
		</td>
		<td></td>
		<td>
			<xsl:value-of select="directory-date"/>
		</td>
		<td>
			<xsl:if test="../canRemove='true'">
			<a class="btn btn-mini btn-danger" href="jsp/site/plugins/jsr170/DoDeleteFile.jsp?portlet_id={../../portlet-id}&#38;view_id_{../../portlet-id}={../current-view-id}&#38;file_id={file-id}&#38;page_id={../../page-id}">
				<!-- img src="images/local/skin/plugins/jsr170/actions/action_delete.png" alt="supprimer" / -->
				<i class="icon-trash icon-white" alt="supprimer" title="Supprimer">&#160;</i>&#160;
			</a>
			</xsl:if>
		</td>
	</tr>
</xsl:template>

<xsl:template match="version">
	<tr>
		<td>
		</td>
  		<td>
			<a href="jsp/site/plugins/jsr170/DisplayVersionnedFile.jsp?portlet_id={../../portlet-id}&#38;view_id_{../../portlet-id}={../current-view-id}&#38;version_name={version-name}&#38;file_id={../file-id}&#38;page_id={../../page-id}">
		        <xsl:value-of select="version-name" />
	        </a>
		</td>
		<td></td>
		<td>
			<xsl:value-of select="version-date"/>
		</td>
		<td>
		</td>
	</tr>
</xsl:template>

<xsl:template match="breadcrumbs">
<li>
	<a href="{$site-path}?portlet_id={/portlet/portlet-id}&#38;page_id={/portlet/page-id}&#38;view_id_{/portlet/portlet-id}={/portlet/jsr170-portlet/current-view-id}&#38;file_id_{/portlet/portlet-id}=#portlet_id_{/portlet/portlet-id}&#38;page_id={../../page-id}" >
		<span class="divider">Home <xsl:text>/</xsl:text></span>
	</a>
</li>
<xsl:apply-templates select="breadcrumb"/>
</xsl:template>

<xsl:template match="breadcrumb">
<li class="active">
<a href="{$site-path}?portlet_id={/portlet/portlet-id}&#38;page_id={/portlet/page-id}&#38;view_id_{/portlet/portlet-id}={/portlet/jsr170-portlet/current-view-id}&#38;file_id_{/portlet/portlet-id}={breadcrumb-path-id}#portlet_id_{/portlet/portlet-id}&#38;page_id={../../page-id}" >
	<xsl:value-of select="breadcrumb-name" /><span class="divider"><xsl:text>/</xsl:text></span>
</a>
</li>
</xsl:template>

<xsl:template match="admin-view-combo">
	<h3>
		Espace de travail :
	    <form name="change_workspace" action="jsp/site/plugins/jsr170/ChangeView.jsp" method="post">
			<input type="hidden" name="portlet_id" value="{../../portlet-id}" />
			<input type="hidden" value="{../current-view-id}" name="old_view_id_{../../portlet-id}" />
			<input type="hidden" name="page_id" value="{../../page-id}" />

			<select name="view_id_{../../portlet-id}">
		        <xsl:apply-templates select="admin-view" />
	        </select>
	        <button type="submit"><i class="icon-ok">&#160;</i>&#160;Modifier</button>
        </form>
    </h3>
</xsl:template>

<xsl:template match="admin-view">
		<xsl:choose>
			<xsl:when test="admin-view-id=../../current-view-id">
				<option value="{admin-view-id}" selected="selected"><xsl:value-of select="admin-view-name"/></option>
			</xsl:when>
			<xsl:otherwise>
				<option value="{admin-view-id}"><xsl:value-of select="admin-view-name"/></option>
			</xsl:otherwise>
		</xsl:choose>
</xsl:template>

<xsl:template match="file-content">
	<tr>
		<td colspan="5">
			<xsl:value-of select="." />
		</td>
	</tr>
</xsl:template>

<xsl:template match="jsr170-portlet-error">
	<xsl:value-of select="." />
</xsl:template>

</xsl:stylesheet>

