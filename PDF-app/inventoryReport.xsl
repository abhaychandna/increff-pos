<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="my-page"
					page-height="29.7cm" page-width="21cm" margin="1cm">
					<fo:region-body margin-top="2cm"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="my-page">


				<fo:flow flow-name="xsl-region-body">
					<fo:block>
						<fo:table>
						<fo:table-body>
							<fo:table-row>
							<fo:table-cell text-align="center">
								<fo:block>
									<fo:external-graphic src="https://www.increff.com/wp-content/themes/increff/new-mega-menu/images/logo-new.png" height="20mm" content-height="scale-to-fit"/>
								</fo:block>
							</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
						</fo:table>
					</fo:block>
				
					<fo:block space-after="1cm"> </fo:block>

					<fo:block>
						<fo:table>
						<fo:table-body>
							<fo:table-row>
							<fo:table-cell text-align="center" font-size="25pt">
								<fo:block>
								<fo:inline> Inventory Report </fo:inline>                   
								</fo:block>
							</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
						</fo:table>
					</fo:block>

					<fo:block space-after="1cm"> </fo:block>
					
					<fo:block>
							<fo:table width="100%"
							border-style="double"
							border-width="1pt"
							border-color="#69468D"
							table-layout="fixed"
							space-before="10px"
							space-after="10px"
							text-align="center"
							font-size="12pt">
							<fo:table-column />
							<fo:table-column />
							<fo:table-column />
							<fo:table-column />
							<fo:table-header
							background-color="#69468D"
							color="white">
								<fo:table-row>
									<fo:table-cell padding-bottom="2px" padding-top="2px">
										<fo:block font-weight="bold">S.No.</fo:block>
									</fo:table-cell>
									<fo:table-cell padding-bottom="2px" padding-top="2px">
										<fo:block font-weight="bold">Brand</fo:block>
									</fo:table-cell>
									<fo:table-cell padding-bottom="2px" padding-top="2px">
										<fo:block font-weight="bold">Category</fo:block>
									</fo:table-cell>
									<fo:table-cell padding-bottom="2px" padding-top="2px">
										<fo:block font-weight="bold">Quantity</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-header>

							
							<fo:table-body>
								<xsl:for-each select="ReportForm/ItemsList/Items">
									<fo:table-row border-style="double" border-width="1pt" border-color="#69468D">
										<fo:table-cell padding-bottom="2px" padding-top="2px">
											<fo:block>
												<xsl:value-of select="position()"/>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding-bottom="2px" padding-top="2px">
											<fo:block>
												<xsl:value-of select="brand"/>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding-bottom="2px" padding-top="2px">
											<fo:block>
												<xsl:value-of select="category"/>
											</fo:block>
										</fo:table-cell>
										<fo:table-cell padding-bottom="2px" padding-top="2px">
											<fo:block>
												<xsl:value-of select="quantity"/>
											</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</xsl:for-each>
							</fo:table-body>
						</fo:table>
					</fo:block>

				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
</xsl:stylesheet>
