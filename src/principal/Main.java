package principal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class Main {

	public static void main(String[] args) throws Throwable {
		// TODO Auto-generated method stub
		
		int result=0;
		ControlDB control = new ControlDB();
		control.cn.setAutoCommit(false);
		
		int smn_documento_id;
		int smn_entidad_rf;
		int smn_sucursal_rf;
		int smn_moneda_rf;
		int smn_documentos_generales_rf;
		int ccc_numero_documento;
		int smn_control_cierre_contable_id;
		int smn_clase_auxiliar_rf;
		int smn_auxiliar_rf;
		int smn_tasa;
		int smn_modulo_rf;
		int smn_tipo_documento_id;
		int smn_rel_modulo_documento_tipo_doc_id;
		int doc_numero_documento;
		int smn_clase_auxiliar_ord_rf;
		int smn_auxiliar_ord_rf;
		int doc_orden_compra_rf;
		int smn_centro_costo_rf;
		int smn_proyecto_rf;
		int smn_documentos_generales_rf_afecta;
		int doc_numero_doc_afecta;
		int doc_numero_control_doc_afect;
		int smn_codigos_impuestos_rf;
		int doc_numero_control_fiscal_inicial;
		int doc_numero_control_fiscal_ultimo;
		int doc_numero_control1_inicial;
		int doc_numero_control1_ultimo;
		int doc_numero_control2_inicial;
		int doc_numero_control2_ultimo;
		int doc_ano_comprobante;
		int doc_periodos_detalles_rf;
		int smn_tipo_comprobante_id;
		int doc_num_comprobante;
		int smn_elemento_rf;
		int smn_documento_id_cont;
		int smn_forma_pago_rf;
		int smn_diccionario_id;
		int smn_tipo_elemento_id;
		int smn_tasa_rf;
		double total_monto_mov_caja_ml=0;
		double total_monto_mov_caja_ma=0;
		double doc_tasa_cambio;
		double total_monto_ml;
		double total_monto_ma;
		double dod_monto_neto_ml;
		double dod_monto_neto_ma;
		java.sql.Date mcc_fecha_registro=null;
		//java.sql.Date doc_fecha_doc;
		java.sql.Date doc_fecha_rec;
		java.sql.Date doc_fecha_vcto;
		java.sql.Date doc_fecha_doc_afecta;
		java.sql.Date doc_fecha_comprobante;
		String ccc_estatus;
		String ccc_idioma;
		String ccc_usuario;
		String doc_estatus;
		String doc_planilla_importacion;
		String doc_numero_control;
		String doc_descripcion;
		String cmp_descripcion;
		String dod_estatus;
		
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
		String fechaActual= sdformat.format(new Date());
		
		String sistemaOperativo = System.getProperty("os.name");
		String file;
		  
		if(sistemaOperativo.equals("Windows 7") || sistemaOperativo.equals("Windows 8") || sistemaOperativo.equals("Windows 10")) 
			file =  "C:/log/logContabilizarMovCaja_"+fechaActual+".txt";
		else
			file = "./logContabilizarMovCaja_"+fechaActual+".txt";
		
		File newLogFile = new File(file);
		FileWriter fw;
		String str="";
		
		if(!newLogFile.exists())
			fw = new FileWriter(newLogFile);
		else
			fw = new FileWriter(newLogFile,true);
		
		BufferedWriter bw=new BufferedWriter(fw);
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
		Date parsed = date.parse(fechaActual);
        java.sql.Date sqlDate = new java.sql.Date(parsed.getTime());
        
        LocalDateTime locaDate = LocalDateTime.now();
        int hours  = locaDate.getHour();
        int minutes = locaDate.getMinute();
        int seconds = locaDate.getSecond();
        String hora = hours+":"+minutes+":"+seconds;
        
		try
		{
			str = "--------"+timestamp+"--------";	
			bw.write(str);
			bw.flush();
			bw.newLine();
			bw.newLine();
			
			str = "--- INICIO DEL PROCESO: 'Contabilizar Movimiento de Caja' ---";	
			bw.write(str);
			bw.flush();
			bw.newLine();
			
			str = "--- Consultando movimientos de caja para generar cierre contable ---";	
			bw.write(str);
			bw.flush();
			bw.newLine();
			
			ResultSet facturas=control.consultarMovCajas();
			
			if(getRecordCount(facturas)>0)
			{
				
				while(facturas.next())
				{	
					mcc_fecha_registro=facturas.getDate("mcc_fecha_registro");
					smn_entidad_rf=facturas.getInt("smn_entidad_rf");
					smn_sucursal_rf=facturas.getInt("smn_sucursal_rf");					
					smn_documento_id=facturas.getInt("smn_documento_id");
					smn_moneda_rf=facturas.getInt("smn_moneda_rf");
					smn_clase_auxiliar_rf=facturas.getInt("smn_clase_auxiliar_rf");
					smn_auxiliar_rf=facturas.getInt("smn_auxiliar_rf");
					total_monto_mov_caja_ml=facturas.getDouble("total_monto_mov_caja_ml");
					total_monto_mov_caja_ma=facturas.getDouble("total_monto_mov_caja_ma");
					smn_documentos_generales_rf=facturas.getInt("smn_documentos_generales_rf");
					doc_descripcion=facturas.getString("doc_descripcion");
					
					ResultSet numero_cierre=control.consultarNumero_ccc();
					
					if(getRecordCount(numero_cierre)>0)
					{
						numero_cierre.next();
						if(numero_cierre.getString("ccc_numero_documento")!=null)
							ccc_numero_documento=numero_cierre.getInt("ccc_numero_documento")+1;
						else
							ccc_numero_documento=1;
					}
					else
					{
						ccc_numero_documento=1;
					}
					
					smn_tasa=0;
					ccc_estatus="CO";
					ccc_idioma="es";
					ccc_usuario="admin";
					
					ResultSet moduloMovCaja=control.moduloMovCaja();
					
					if(getRecordCount(moduloMovCaja)>0)
					{
						moduloMovCaja.next();
						smn_modulo_rf=moduloMovCaja.getInt("smn_modulos_id");
					}
					else
					{
						str = "--- No se encontro el modulo SMN_CAJ=Caja ---";	
						bw.write(str);
						bw.flush();
						bw.newLine();
						result=1;
						break;
					}
					
					str = "--- Registrando cierre contable ---";	
					bw.write(str);
					bw.flush();
					bw.newLine();
					
					ResultSet RegistrarControlCierre=control.insertControl_cierre(smn_entidad_rf, smn_sucursal_rf, smn_documentos_generales_rf, smn_documento_id, mcc_fecha_registro, ccc_numero_documento, total_monto_mov_caja_ml, total_monto_mov_caja_ma, smn_moneda_rf, smn_tasa, ccc_estatus, ccc_idioma, ccc_usuario, sqlDate, hora, smn_modulo_rf,smn_clase_auxiliar_rf,smn_auxiliar_rf);
					RegistrarControlCierre.next();
					
					smn_control_cierre_contable_id=RegistrarControlCierre.getInt("smn_control_cierre_contable_id");
					
					str = "--- Cierre contable registrado ---";	
					bw.write(str);
					bw.flush();
					bw.newLine();
					
					str = "--- Actualizando movimientos de caja con el cierre contable registrado ---";	
					bw.write(str);
					bw.flush();
					bw.newLine();
					
					control.updateMovCajaCabecera(smn_entidad_rf, smn_sucursal_rf, smn_documento_id, smn_moneda_rf, mcc_fecha_registro, smn_control_cierre_contable_id);
					
					str = "--- Movimientos de Caja actualizadas ---";	
					bw.write(str);
					bw.flush();
					bw.newLine();
					
					ResultSet tipoDocumento=control.tipoDocumento(smn_modulo_rf, smn_documentos_generales_rf);
					
					if(getRecordCount(tipoDocumento)>0)
					{
						tipoDocumento.next();
						smn_tipo_documento_id=tipoDocumento.getInt("smn_tipo_documento_id");
						smn_rel_modulo_documento_tipo_doc_id=tipoDocumento.getInt("smn_rel_modulo_documento_tipo_doc_id");
					}
					else
					{
						str = "--- No se encontro el tipo de documento contable ---";	
						bw.write(str);
						bw.flush();
						bw.newLine();
						result=1;
						break;
					}
					
					ResultSet numeroDoc=control.numeroDocumento();
					
					if(getRecordCount(numeroDoc)>0)
					{
						numeroDoc.next();
						if(numeroDoc.getString("doc_numero_documento")!=null)
							doc_numero_documento=numeroDoc.getInt("doc_numero_documento")+1;
						else
							doc_numero_documento=1;
					}
					else
					{
						doc_numero_documento=1;
					}
					
					smn_clase_auxiliar_ord_rf=0;
					smn_auxiliar_ord_rf=0;
					doc_orden_compra_rf=0;
					smn_centro_costo_rf=0;
					smn_proyecto_rf=0;
					//doc_fecha_doc=sqlDate;
					doc_fecha_rec=sqlDate;
					doc_fecha_vcto=null;
					doc_planilla_importacion=null;
					smn_documentos_generales_rf_afecta=0;
					doc_numero_doc_afecta=0;
					doc_numero_control_doc_afect=0;
					doc_fecha_doc_afecta=null;
					smn_codigos_impuestos_rf=0;
					doc_numero_control_fiscal_inicial=0;
					doc_numero_control_fiscal_ultimo=0;
					doc_numero_control1_inicial=0;
					doc_numero_control1_ultimo=0;
					doc_numero_control2_inicial=0;
					doc_numero_control2_ultimo=0;
					doc_estatus="R";
					doc_ano_comprobante=0;
					doc_periodos_detalles_rf=0;

					ResultSet tipoComprobante=control.tipoComprobante(smn_rel_modulo_documento_tipo_doc_id);
					
					if(getRecordCount(tipoComprobante)>0)
					{
						tipoComprobante.next();
						smn_tipo_comprobante_id=tipoComprobante.getInt("mcc_tipo_comp");
					}
					else
					{
						str = "--- No se encontro el tipo de comprobante contable ---";	
						bw.write(str);
						bw.flush();
						bw.newLine();
						result=1;
						break;
					}
					
					doc_num_comprobante=0;
					doc_fecha_comprobante=null;
					doc_numero_control=null;
					smn_elemento_rf=0;
					//doc_descripcion=null;
					//smn_moneda_rf=0;
					
					if (total_monto_mov_caja_ml != 0 && total_monto_mov_caja_ma != 0 ){
						doc_tasa_cambio=total_monto_mov_caja_ml/total_monto_mov_caja_ma;
					}
					else {
						doc_tasa_cambio = 0;
					}
					
					//doc_tasa_cambio=total_monto_mov_caja_ml/total_monto_mov_caja_ma;
					
					str = "--- Registrando documento contable ---";	
					bw.write(str);
					bw.flush();
					bw.newLine();
					
					ResultSet insertDoc=control.insertDocumento(smn_modulo_rf, smn_entidad_rf, smn_sucursal_rf, 
							smn_documentos_generales_rf, smn_tipo_documento_id, doc_numero_documento, 
							smn_clase_auxiliar_rf, smn_auxiliar_rf, smn_clase_auxiliar_ord_rf, 
							smn_auxiliar_ord_rf, doc_orden_compra_rf, smn_centro_costo_rf, smn_proyecto_rf, 
							mcc_fecha_registro, doc_fecha_rec, doc_fecha_vcto, doc_planilla_importacion, 
							total_monto_mov_caja_ml, total_monto_mov_caja_ma, doc_tasa_cambio, smn_documentos_generales_rf_afecta, 
							doc_numero_doc_afecta, doc_numero_control_doc_afect, doc_fecha_doc_afecta, 
							smn_codigos_impuestos_rf, doc_numero_control_fiscal_inicial, doc_numero_control_fiscal_ultimo, 
							doc_numero_control1_inicial, doc_numero_control1_ultimo, doc_numero_control2_inicial, 
							doc_numero_control2_ultimo, doc_estatus, doc_ano_comprobante, doc_periodos_detalles_rf, 
							smn_tipo_comprobante_id, doc_num_comprobante, "es", "admin", sqlDate, hora, doc_fecha_comprobante,
							doc_numero_control, smn_elemento_rf, doc_descripcion, 0);
					
					str = "--- Documento contable registrado ---";	
					bw.write(str);
					bw.flush();
					bw.newLine();
					
					insertDoc.next();
					smn_documento_id_cont=insertDoc.getInt("smn_documento_id_cont");
					
					str = "Datos para la consultarDetalles" + "Entidad: " + smn_entidad_rf + " Sucursal: " + smn_sucursal_rf + " Documento_id; " +  smn_documento_id + " Moneda_rf: " +  smn_moneda_rf + " Fecha_registro: " + mcc_fecha_registro;	
					bw.write(str);
					bw.flush();
					bw.newLine();
					
					ResultSet detalles=control.consultarDetalles(smn_entidad_rf, smn_sucursal_rf, smn_documento_id, smn_moneda_rf, mcc_fecha_registro);
					
					if(getRecordCount(detalles)>0)
					{
						while(detalles.next())
						{
							
							smn_forma_pago_rf=detalles.getInt("smn_forma_pago_rf");
							
							/* if(detalles.getString("centro_costo")!=null)
								smn_centro_costo_rf=detalles.getInt("centro_costo");
							else
							{
								str = "*No se encontro el centro de costo*";	
								bw.write(str);
								bw.flush();
								bw.newLine();
								result=1;
								break;
							} */
							
							/*if(detalles.getString("smn_clase_auxiliar_rf")!=null)
								smn_clase_auxiliar_rf=detalles.getInt("smn_clase_auxiliar_rf");
							else
							{ */
								/*str = "*No se encontro la clase auxiliar*";	
								bw.write(str);
								bw.flush();
								bw.newLine();
								result=1;
								break;*/
							/*	smn_clase_auxiliar_rf=0;
							} */
							
							/*if(detalles.getString("smn_auxiliar_rf")!=null)
								smn_auxiliar_rf=detalles.getInt("smn_auxiliar_rf");
							else
							{
								/*str = "*No se encontro el auxiliar*";	
								bw.write(str);
								bw.flush();
								bw.newLine();
								result=1;
								break;*/
								/*smn_auxiliar_rf=0;
							} */
							
							total_monto_ml=detalles.getDouble("total_monto_detalle_mov_caja_ml");
							
							if(detalles.getString("total_monto_detalle_mov_caja_ma")!=null)
								total_monto_ma=detalles.getDouble("total_monto_detalle_mov_caja_ma");
							else
								total_monto_ma=0;
								
							ResultSet diccionario=control.consultarDiccionario();
							
							
							if(getRecordCount(diccionario)>0)
							{
								diccionario.next();
								smn_diccionario_id=diccionario.getInt("smn_diccionario_id");
							}
							else
							{
								str = "*No se encontro el diccionario*";	
								bw.write(str);
								bw.flush();
								bw.newLine();
								result=1;
								break;
							}
							
							ResultSet tipoElemento=control.consultarTipoElemento(smn_diccionario_id, smn_modulo_rf);
							
							if(getRecordCount(tipoElemento)>0)
							{
								tipoElemento.next();
								smn_tipo_elemento_id=tipoElemento.getInt("smn_tipo_elemento_id");
							}
							else
							{
								str = "*No se encontro el tipo de elemento*";	
								bw.write(str);
								bw.flush();
								bw.newLine();
								result=1;
								break;
							}
							
							smn_tasa_rf=0;
							
							ResultSet componente=control.consultarComponente(smn_forma_pago_rf);
							
							if(getRecordCount(componente)>0)
							{
								componente.next();
								cmp_descripcion=componente.getString("cmp_descripcion");
							}
							else
							{
								str = "*No se encontro el componente*";	
								bw.write(str);
								bw.flush();
								bw.newLine();
								result=1;
								break;
							}
							
							dod_monto_neto_ml=total_monto_ml;
							dod_monto_neto_ma=total_monto_ma;
							dod_estatus="RE";
							
							str = "--- Registrando documento detalle ---";	
							bw.write(str);
							bw.flush();
							bw.newLine();
							
							control.insertDocumentoDetalle(smn_documento_id_cont, smn_tipo_elemento_id, 
									smn_forma_pago_rf, smn_clase_auxiliar_rf, smn_auxiliar_rf, 
									smn_proyecto_rf, smn_centro_costo_rf, total_monto_ml, smn_moneda_rf, 
									smn_tasa_rf, total_monto_ma, dod_estatus, "es", "admin", sqlDate, 
									hora, dod_monto_neto_ml, dod_monto_neto_ma, cmp_descripcion);
							
							str = "--- Documento detalle registrado ---";	
							bw.write(str);
							bw.flush();
							bw.newLine();
							
						} //end while detalles
					}
					else
					{
						str = "--- No se encontraron detalles para procesar ---";	
						bw.write(str);
						bw.flush();
						bw.newLine();
						result=1;
						break;
					}
					
					if(result==0)
					{
						str = "--- Actualizando estatus de los movimientos de caja ---";	
						bw.write(str);
						bw.flush();
						bw.newLine();
						
						control.updateMovCaja("CO", smn_entidad_rf, smn_sucursal_rf, smn_documento_id, smn_moneda_rf, mcc_fecha_registro);
						
						str = "--- Estatus de los movimientos de caja actualizados";	
						bw.write(str);
						bw.flush();
						bw.newLine();
						
					}
					else
					{
						break;
					}
					
				}//end while facturas
			}
			else
			{
				str = "--- No se encontraron facturas para procesar ---";	
				bw.write(str);
				bw.flush();
				bw.newLine();
				result=1;
			}
		}
		catch(Throwable e)
		{
			control.cn.rollback();
			throw e;
		}
		finally
		{
			if(result == 0)
			{
				control.cn.commit();
				str = "--- Cambios efectuados en la base de datos ---";	
				bw.write(str);
				bw.flush();
				bw.newLine();
			}
			else
			{
				control.cn.rollback();
				str = "--- Cambios no efectuados en la base de datos ---";	
				bw.write(str);
				bw.flush();
				bw.newLine();
			}
			
			if(control.cn!=null)
				control.cn.close();
			
			str = "FINAL DEL PROCESO";	
			bw.write(str);
			bw.flush();
			bw.newLine();
			
			bw.close();
		}
	}
	
	public static int getRecordCount(ResultSet rs)
	{
		int total=0;
		
		try {
			boolean ultimo = rs.last();
			
			if (ultimo)
			{
		        total = rs.getRow();
		        rs.beforeFirst();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return total;
	}

}
