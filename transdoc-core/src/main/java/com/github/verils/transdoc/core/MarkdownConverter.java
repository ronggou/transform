package com.github.verils.transdoc.core;

import com.github.verils.transdoc.core.model.Article;
import com.github.verils.transdoc.core.model.Part;
import com.github.verils.transdoc.core.model.Paragraph;
import com.github.verils.transdoc.core.model.Picture;
import java.util.List;

import com.github.verils.transdoc.core.model.Table;

public class MarkdownConverter implements Convertor {
	private int one;
	private int two;
	private int three;
	private int four;
	private int five;
	private int six;
	private int pre = 0;

	@Override
	public String convert(Article article) {
		if (article == null) {
			return "";
		}

		List<Part> parts = article.getParts();
		StringBuilder markdown = new StringBuilder();

		for (Part part : parts) {
			boolean isInList = part.isInList();
			if (isInList) {
				markdown.append("\t");
			}

			switch (part.getType()) {
				case PARAGRAPH: {
					Paragraph paragraph = (Paragraph) part;
					String content = paragraph.getContent();
					int titleLvl = paragraph.getTitleLvl();
					int listLvl = paragraph.getListLvl();
					if (titleLvl > 0) {
						switch(titleLvl) {
							case 1: {
								one++;
								break;
							}
							case 2: {
								two++;
								break;
							}
							case 3: {
								three++;
								break;
							}
							case 4: {
								four++;
								break;
							}
							case 5: {
								five++;
								break;
							}
							case 6: {
								six++;
								break;
							}
						}

						if(titleLvl != pre) {
							switch(titleLvl) {
								case 1: {
									two = 0;
									three = 0;
									four = 0;
									five = 0;
									six = 0;
									break;
								}
								case 2: {
									three = 0;
									four = 0;
									five = 0;
									six = 0;
									break;
								}
								case 3: {
									four = 0;
									five = 0;
									six = 0;
									break;
								}
								case 4: {
									five = 0;
									six = 0;
									break;
								}
								case 5: {
									six = 0;
									break;
								}
								case 6: {
									break;
								}
							}
						}
						// 标题段落
						for (int i = 0; i < titleLvl; i++) {
							markdown.append("#");
						}
						markdown.append(" ");
						if(one > 0) {
							markdown.append(one + ".");
						}
						if(two > 0) {
							markdown.append(two + ".");
						}
						if(three > 0) {
							markdown.append(three + ".");
						}
						if(four > 0) {
							markdown.append(four + ".");
						}
						if(five > 0) {
							markdown.append(five + ".");
						}
						if(six > 0) {
							markdown.append(six + ".");
						}
						pre = titleLvl;
						content = content.replaceAll("[0-9.]", "");
					} else if (isInList || listLvl > 0) {
						// 列表段落
						markdown.append(listLvl).append(". ");
					}
					markdown.append(escapeHTML(content));
					break;
				}

			case TABLE: {
				Table table = (Table) part;
				StringBuilder tableContent = new StringBuilder();
				if (table.isBlock()) {
					Article cell = table.getCell(0, 0);
					tableContent.append("```").append("\n");
					for (Paragraph docParagraph : cell.getParagraphs()) {
						tableContent.append(docParagraph.getContent()).append("\n");
					}
					tableContent.append("```");
				} else {
					int rownum = table.getRownum();
					int colnum = table.getColnum();
					for (int i = 0; i < rownum; i++) {
						tableContent.append("|");
						for (int j = 0; j < colnum; j++) {
							Article cell = table.getCell(i, j);
							String cellContent = convertCell(cell);
							cellContent = cellContent.trim().replaceAll("\n", "<br>");
							tableContent.append(cellContent).append("|");
						}
						if (isInList) {
							tableContent.append("\n").append("\t");
						}
						// 添加表格第二行分隔行
						if (i == 0) {
							tableContent.append("\n").append("|");
							for (int j = 0; j < colnum; j++) {
								tableContent.append("----|");
							}
						}
						tableContent.append("\n");
					}
					// 删除最后的换行符
					tableContent.deleteCharAt(tableContent.length() - 1);
				}
				markdown.append(tableContent);
				break;
			}

			case PICTURE: {
				Picture picture = (Picture) part;
				markdown.append("![](").append(picture.getRelativePath()).append(")");
				break;
			}

			default:
				break;
			}
			markdown.append("\n\n");
		}
		return markdown.toString();
	}

	private String convertCell(Article cell) {
		return convert(cell);
	}

	private String escapeHTML(String content) {
		content = content.replaceAll("&", "&amp;");
		content = content.replaceAll("<", "&lt;");
		content = content.replaceAll(">", "&gt;");
		return content;
	}

}